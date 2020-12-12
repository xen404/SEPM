package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import at.ac.tuwien.sepm.groupphase.backend.exception.IllegalOperationException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.*;
import at.ac.tuwien.sepm.groupphase.backend.service.ShowService;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import util.GeneratePdfReport;

import java.io.ByteArrayInputStream;
import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class SimpleShowService implements ShowService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final ShowRepository showRepository;
    private final TicketRepository ticketRepository;
    private final SeatRepository seatRepository;
    private final EventRepository eventRepository;
    private final LocationRepository locationRepository;
    private final UsersRepository usersRepository;
    private final UserService userService;


    @Autowired
    public SimpleShowService(ShowRepository showRepository, TicketRepository ticketRepository,
                             SeatRepository seatRepository, EventRepository eventRepository,
                             LocationRepository locationRepository, UsersRepository usersRepository,
                             UserService userService) {
        this.showRepository = showRepository;
        this.ticketRepository = ticketRepository;
        this.seatRepository = seatRepository;
        this.eventRepository = eventRepository;
        this.locationRepository = locationRepository;
        this.usersRepository = usersRepository;
        this.userService = userService;
    }

    @Override
    public List<Show> findAll() {
        LOGGER.debug("Find all shows");
        return showRepository.findAll();
    }

    @Override
    public Page<Show> findAllAsPage(Pageable pageable) {
        LOGGER.debug("Find all shows as page");
        return showRepository.findAllByOrderByIdDesc(pageable);
    }

    @Override
    public List<Ticket> getTicketsOfShow(Long id) {
        LOGGER.debug("Get all tickets of show with id {}", id);
        Optional<Show> show = showRepository.findById(id);
        if (show.isPresent()) {
            return ticketRepository.findByShow(show.get());
        } else {
            LOGGER.warn("Could not find show with id {}", id);
            throw new NotFoundException(String.format("Could not find show with id %s", id));
        }
    }

    @Override
    public List<Seat> getSeatsOfShow(Long id) {
        LOGGER.debug("Get all seats of show with id {}", id);
        Optional<Show> show = showRepository.findById(id);
        if (show.isPresent()) {
            Location location = show.get().getLocation();
            return seatRepository.findByLocation(location);
        } else {
            LOGGER.warn("Could not find show with id {}", id);
            throw new NotFoundException(String.format("Could not find show with id %s", id));
        }
    }

    @Override
    public Map<Seat, Ticket> getSeatsWithTicket(Long id) {
        LOGGER.debug("Get a map of seats with corresponding tickets of show with id {}", id);
        Optional<Show> show = showRepository.findById(id);
        if (show.isPresent()) {
            Map<Seat, Ticket> map = new HashMap<>();
            List<Seat> seats = getSeatsOfShow(id);
            List<Ticket> tickets = getTicketsOfShow(id);
            seats.forEach(
                seat -> map.put(seat,
                    tickets.stream()
                        .filter(ticket -> ticket.getSeat().getId().equals(seat.getId()))
                        .findAny()
                        .orElse(null))
            );
            return map;
        } else {
            LOGGER.warn("Could not find show with id {}", id);
            throw new NotFoundException(String.format("Could not find show with id %s", id));
        }
    }

    @Override
    public Show findOne(Long id) {
        LOGGER.debug("Get the show with id {}", id);
        Optional<Show> show = showRepository.findById(id);
        if (show.isPresent()) {
            return show.get();
        } else {
            LOGGER.warn("Could not find show with id {}", id);
            throw new NotFoundException(String.format("Could not find show with id %s", id));
        }
    }

    @Override
    public Show saveShow(SaveShow saveShow) {
        LOGGER.debug("Save the show with title {}", saveShow.getShow().getTitle());
        //Validation
        Show show = saveShow.getShow();
        if (show == null) {
            LOGGER.warn("Show must be set");
            throw new ValidationException("Show must be set");
        }
        List<Ticket> tickets = saveShow.getTickets();
        if (tickets == null) {
            LOGGER.warn("Tickets must be set");
            throw new ValidationException("Tickets must be set");
        }
        Map<Character, Float> sectorPricesMap = new HashMap<>();
        Optional<Event> optionalEvent = eventRepository.findById(show.getEvent().getId());
        if (optionalEvent.isEmpty()) {
            LOGGER.warn("There is no event with id {}", show.getEvent().getId());
            throw new ValidationException("There is no event with id " + show.getEvent().getId() + ".");
        }
        if (locationRepository.findById(show.getLocation().getId()).isEmpty()) {
            LOGGER.warn("There is no location with id = {}", show.getLocation().getId());
            throw new ValidationException("There is no location with id " + show.getLocation().getId() + ".");
        }
        List<Seat> seats = seatRepository.findByLocation(show.getLocation());
        for (Seat seat : seats) {
            if (seat.isRealSeat()) {
                Optional<Ticket> ticket = tickets.stream().filter((item) -> item.getSeat().getId()
                    .equals(seat.getId())).findAny();
                if (ticket.isEmpty()) {
                    LOGGER.warn("There must be a ticket for the show for every seat.");
                    throw new ValidationException("There must be a ticket for the show for every seat.");
                }
                if (!sectorPricesMap.containsKey(ticket.get().getSector())) {
                    sectorPricesMap.put(ticket.get().getSector(), ticket.get().getPrice());
                } else {
                    if (sectorPricesMap.get(ticket.get().getSector()) != ticket.get().getPrice()) {
                        LOGGER.warn("Seats in the same sector must have the same prices.");
                        throw new ValidationException("Seats in the same sector must have the same prices.");
                    }
                }
            }
        }

        int duration = optionalEvent.get().getDuration();
        LocalDateTime endDateTime = LocalDateTime.of(show.getStartDate(), show.getStartTime()).plusMinutes(duration);
        show.setEndDate(endDateTime.toLocalDate());
        show.setEndTime(endDateTime.toLocalTime());

        Show savedShow = showRepository.save(show);
        for (Ticket ticket : saveShow.getTickets()) {
            ticketRepository.save(ticket);
        }
        return savedShow;
    }

    @Override
    @Transactional
    public Long[] orderTickets(List<Long> seatIds, Long showId, String userEmail, String mode) {
        LOGGER.debug("Order tickets (mode: {}) for the show with id {} for the seats {} for the user {}",
            mode, showId, seatIds, userEmail);
        Optional<Show> showOptional = showRepository.findById(showId);
        if (showOptional.isEmpty()) {
            LOGGER.warn("Could not find show with id {}", showId);
            throw new NotFoundException(String.format("Could not find show with id %s", showId));
        }

        LocalDateTime endDateTime = LocalDateTime.of(showOptional.get().getEndDate(), showOptional.get().getEndTime());
        if (LocalDateTime.now().isAfter(endDateTime)) {
            LOGGER.warn("Cannot order tickets for a show that has already taken place.");
            throw new ValidationException("Cannot order tickets for a show that has already taken place.");
        }

        ApplicationUser user = usersRepository.findByEmail(userEmail);
        if (user == null) {
            LOGGER.warn("User that wants to purchase tickets must exist.");
            throw new ValidationException("User that wants to purchase tickets must exist.");
        }

        if (mode.equals("PURCHASE")) {
            return purchaseTickets(seatIds, showOptional.get(), user);
        }

        if (mode.equals("RESERVE")) {
            return new Long[]{reserveTickets(seatIds, showOptional.get(), user), 0L};
        }

        return null;
    }

    @Transactional
    public Long[] purchaseTickets(List<Long> seatIds, Show show, ApplicationUser user) {
        LOGGER.debug("Purchase tickets for the show {} for the seats {} for the user {}", show, seatIds, user);
        List<Ticket> tickets = ticketRepository.findByShow(show);
        Long orderId = ticketRepository.getNextOrderId();
        float totalSum = 0.0f;
        for (Long seatId : seatIds) {
            Ticket ticket = tickets.stream().filter((item) -> item.getSeat().getId()
                .equals(seatId)).findFirst().orElse(null);
            if (ticket == null) {
                LOGGER.warn("Cannot purchase tickets for seats that don't exist.");
                throw new ValidationException("Cannot purchase tickets for seats that don't exist.");
            }

            if (ticket.getStatus() != Ticket.Status.FREE) {
                LOGGER.warn("Cannot purchase tickets that are not available anymore.");
                throw new ValidationException("Cannot purchase tickets that are not available anymore.");
            }

            ticket.setUser(user);
            ticket.setOrderId(orderId);
            ticket.setDateOfPurchase(LocalDate.now());
            ticket.setStatus(Ticket.Status.PURCHASED);
            ticketRepository.save(ticket);
            totalSum += ticket.getPrice();
        }
        Long bonusPointsToAdd = (Math.round(totalSum * 0.1));
        LOGGER.debug("Collected bonus points for user {} for order {}: {}", user, orderId, bonusPointsToAdd);
        userService.updateBonusPoints(user.getId(), bonusPointsToAdd);
        return new Long[]{orderId, bonusPointsToAdd};
    }

    @Transactional
    public Long reserveTickets(List<Long> seatIds, Show show, ApplicationUser user) {
        LOGGER.debug("Reserve tickets for the show {} for the seats {} for the user {}", show, seatIds, user);
        List<Ticket> tickets = ticketRepository.findByShow(show);
        Long orderId = ticketRepository.getNextOrderId();
        for (Long seatId : seatIds) {
            Ticket ticket = tickets.stream().filter((item) -> item.getSeat().getId()
                .equals(seatId)).findFirst().orElse(null);
            if (ticket == null) {
                LOGGER.warn("Cannot reserve tickets for seats that don't exist.");
                throw new ValidationException("Cannot reserve tickets for seats that don't exist.");
            }

            if (ticket.getStatus() != Ticket.Status.FREE) {
                LOGGER.warn("Cannot reserve tickets that are not available anymore.");
                throw new ValidationException("Cannot reserve tickets that are not available anymore.");
            }

            ticket.setUser(user);
            ticket.setOrderId(orderId);
            ticket.setDateOfReservation(LocalDate.now());
            ticket.setStatus(Ticket.Status.RESERVED);
            ticketRepository.save(ticket);
        }
        return orderId;
    }

    @Override
    @Transactional
    public Long purchaseReservedTickets(List<Long> ticketIds, String userEmail) {
        LOGGER.debug("Purchase previously reserved tickets {} for the user with email {}", ticketIds, userEmail);

        ApplicationUser user = usersRepository.findByEmail(userEmail);
        if (user == null) {
            LOGGER.warn("User that wants to purchase previously reserved tickets must exist.");
            throw new ValidationException("User that wants to purchase previously reserved tickets must exist.");
        }

        float totalSum = 0.0f;
        List<Long> orderIds = new ArrayList<>();
        Long orderId = ticketRepository.getNextOrderId();

        for (Long ticketId : ticketIds) {
            Optional<Ticket> optionalTicket = ticketRepository.findById(ticketId);
            if (optionalTicket.isEmpty()) {
                LOGGER.warn("Cannot purchase tickets that don't exist.");
                throw new ValidationException("Cannot purchase tickets that don't exist.");
            }

            Ticket ticket = optionalTicket.get();
            if (!ticket.getUser().getId().equals(user.getId())) {
                LOGGER.warn("Cannot purchase tickets of another user.");
                throw new ValidationException("Cannot purchase tickets of another user.");
            }

            if (ticket.getStatus() != Ticket.Status.RESERVED) {
                LOGGER.warn("Cannot purchase tickets that are not reserved.");
                throw new ValidationException("Cannot purchase tickets that are not reserved.");
            }

            LocalDateTime endDateTime = LocalDateTime.of(ticket.getShow().getEndDate(), ticket.getShow().getEndTime());
            if (LocalDateTime.now().isAfter(endDateTime)) {
                LOGGER.warn("Cannot order tickets for a show that has already taken place.");
                throw new ValidationException("Cannot order tickets for a show that has already taken place.");
            }

            orderIds.add(ticket.getOrderId());
            ticket.setOrderId(orderId);
            ticket.setDateOfPurchase(LocalDate.now());
            ticket.setStatus(Ticket.Status.PURCHASED);
            ticketRepository.save(ticket);
            totalSum += ticket.getPrice();
        }

        for (Long oid : orderIds) {
            List<Ticket> tickets = ticketRepository.findByOrderId(oid);
            for (Ticket ticket : tickets) {
                if (ticket.getStatus() != Ticket.Status.PURCHASED) {
                    ticket.setStatus(Ticket.Status.FREE);
                    ticket.setOrderId(null);
                    ticket.setUser(null);
                    ticket.setDateOfReservation(null);
                }
            }
        }

        Long bonusPointsToAdd = (Math.round(totalSum * 0.1));
        LOGGER.debug("Collected bonus points for user {}: {}", user, bonusPointsToAdd);
        userService.updateBonusPoints(user.getId(), bonusPointsToAdd);

        return orderId;
    }

    @Override
    public List<Ticket> getTicketsForUserByOrderId(Long orderId, String userEmail) {

        ApplicationUser user = usersRepository.findByEmail(userEmail);
        if (user == null) {
            LOGGER.warn("User that wants to reserve tickets must exist.");
            throw new ValidationException("User that wants to reserve tickets must exist.");
        }

        return ticketRepository.getTicketsForUserByOrderId(user.getId(), orderId);
    }

    @Override
    public long cancelTickets(List<Long> ticketsId, String userEmail) throws IllegalAccessError {

        float totalSum = 0.0f;

        ApplicationUser user = usersRepository.findByEmail(userEmail);
        if (user == null) {
            LOGGER.warn("User does not exist.");
            throw new ValidationException("User does not exist.");
        }

        for (Long id : ticketsId) {

            Optional<Ticket> ticket = ticketRepository.findById(id);
            if (ticket.isEmpty()) {
                LOGGER.warn("Could not find ticket with id {}", id);
                throw new NotFoundException(String.format("Could not find ticket with id %s", id));
            }

            if (ticket.get().getOrderId() == null) {
                LOGGER.warn("Current ticket has no order Id");
                throw new ValidationException("Current ticket has no order Id");
            }

            if (ticket.get().getShow().getStartDate().compareTo(LocalDate.now()) < 0) {
                LOGGER.warn("Current ticket is expired. Show has already passed!");
                throw new ValidationException("Current ticket is expired. Show has already passed!");
            }

            if (ticket.get().getUser() == null) {
                LOGGER.warn("The ticket with id = {} was not assigned to any user", id);
                throw new NotFoundException(String.format("The ticket with id=%s was not assigned to any user", id));
            }

            if (!user.getId().equals(ticket.get().getUser().getId())) {
                LOGGER.warn("Cannot cancel ticket {} {} {}", id,
                    user, ticket.get().getUser().getId());
                throw new IllegalAccessError(String.format("Cannot cancel ticket %s %s %s", id,
                    user, ticket.get().getUser().getId()));
            }

            ticket.get().setUser(null);
            if (ticket.get().getStatus().compareTo(Ticket.Status.PURCHASED) == 0) {
                ticket.get().setStatus(Ticket.Status.FREE);
                ticket.get().setDateOfPurchase(null);
                ticket.get().setOrderId(null);
                totalSum += ticket.get().getPrice();
            } else if (ticket.get().getStatus().compareTo(Ticket.Status.RESERVED) == 0) {
                ticket.get().setStatus(Ticket.Status.FREE);
                ticket.get().setDateOfReservation(null);
                ticket.get().setOrderId(null);
            } else {
                LOGGER.warn("The ticket with id = {} was not purchased", id);
                throw new NotFoundException(String.format("The ticket with id %s was not purchased", id));
            }

            ticketRepository.save(ticket.get());
        }


        long bonusPointsToReduce = (Math.round(totalSum * 0.1));
        LOGGER.debug("Reducing {} bonus points from user account with id {}", bonusPointsToReduce, user.getId());

        try {
            userService.updateBonusPoints(user.getId(), bonusPointsToReduce * -1);
        } catch (IllegalOperationException e) {
            LOGGER.warn("Trying to reduce more bonus points than the user has.");
            LOGGER.debug("Setting user's bonus points to zero.");

            Long usersBonusPoints = user.getBonusPoints();
            userService.updateBonusPoints(user.getId(), usersBonusPoints * -1);

            //TODO: charge the user with according price (--> put in the cancel ticket pdf)
            return bonusPointsToReduce - usersBonusPoints;
        }
        return 0L;
    }

    public ResponseEntity<InputStreamResource> ticketCancelReport(Long orderId,
                                                                  List<Long> ids,
                                                                  String email,
                                                                  Long bonusPoints) {

        List<Ticket> currentTickets;
        Optional<Ticket> ticket;
        List<Ticket> canceledTickets = new ArrayList<>();

        for (Long id : ids) {
            ticket = ticketRepository.findById(id);

            if (ticket.isEmpty()) {
                LOGGER.warn("Could not find ticket with id {}", id);
                throw new NotFoundException(String.format("Could not find ticket with id %s", id));
            }

            canceledTickets.add(ticket.get());
        }

        ApplicationUser user = usersRepository.findByEmail(email);
        if (user == null) {
            LOGGER.warn("User does not exist.");
            throw new ValidationException("User does not exist.");
        }

        currentTickets = getTicketsForUserByOrderId(orderId, user.getEmail());


        if (currentTickets.isEmpty()) {
            currentTickets = new ArrayList<>(canceledTickets);
        } else {
            currentTickets.addAll(canceledTickets);
        }


        ByteArrayInputStream bis = GeneratePdfReport.ticketsCancelReport(currentTickets,
            canceledTickets, user, orderId, bonusPoints);
        var headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=cancelticket.pdf");

        return ResponseEntity
            .ok()
            .headers(headers)
            .contentType(MediaType.APPLICATION_PDF)
            .body(new InputStreamResource(bis));


    }

    public ResponseEntity<InputStreamResource> ticketPurchaseReport(Long orderId, String email) {


        List<Ticket> tickets = getTicketsForUserByOrderId(orderId, email);

        if (tickets.isEmpty()) {
            LOGGER.warn("No tickets available for this order = {}", orderId);
            throw new ValidationException("No tickets available for this order");
        }


        ByteArrayInputStream bis = GeneratePdfReport.ticketsPurchaseReport(tickets);

        var headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=ticket.pdf");

        return ResponseEntity
            .ok()
            .headers(headers)
            .contentType(MediaType.APPLICATION_PDF)
            .body(new InputStreamResource(bis));

    }


    @Override
    public List<Ticket> getAllTicketsByUserId(Long id) {
        return ticketRepository.getTicketsForUserByUserId(id);
    }

    @Override
    public Page<Show> findAllByOrderByTitle(int page) {
        LOGGER.debug("Find all shows with pagination");
        Sort sort = Sort.by("title");
        Pageable pageable = PageRequest.of(page, 15, sort);
        Page<Show> pages = showRepository.findAllByOrderByTitle(pageable);
        if (!pages.isEmpty()) {
            return pages;
        } else {
            throw new NotFoundException("No shows were found.");
        }
    }

    @Override
    public Page<Show> findByParams(String title, String location, int price, String date, String time, Integer page) {
        LOGGER.trace("findByTitleContainingIgnoreCase({})", title);
        Sort sort = Sort.by("title");
        Pageable p = PageRequest.of(page, 15, sort);
        Page<Show> pages = showRepository.findByParams(title, location, price, date, time, p);
        if (!pages.isEmpty()) {
            return pages;
        } else {
            throw new NotFoundException("No shows fitting search criteria were found.");
        }
    }

    @Override
    public Page<Show> findShowsByLocation(Long id, Pageable pageable) {
        LOGGER.debug("findAllEventsForCurrentLocation({})", id);
        return showRepository.findAllByLocation_Id(id, pageable);
    }

    @Override
    public Page<Ticket> getPageWithAllTicketsByUserId(Long id, int page) {
        LOGGER.debug("Find all tickets with pagination");
        Sort sort = Sort.by("order_id");
        Pageable pageable = PageRequest.of(page, 25, sort);
        return ticketRepository.getPageWithAllTicketsByUserId(id, pageable);
    }
}
