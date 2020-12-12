package at.ac.tuwien.sepm.groupphase.backend.datagenerator;

import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import at.ac.tuwien.sepm.groupphase.backend.repository.ShowRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.TicketRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UsersRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.ShowService;
import com.github.javafaker.Faker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.time.Month;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * ZTicketDataGenerator generates ordinary tickets.
 * It starts with Z so that UserDataGenerator is executed before, because Tickets are dependent of Users.
 */

@Profile("generateData")
@Component
public class ZTicketDataGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final List<Ticket.Status> VALUES = List.of(Ticket.Status.values());
    private static final int SIZE = VALUES.size();
    private static final Random RANDOM = new Random();

    private final ShowRepository showRepository;
    private final TicketRepository ticketRepository;
    private final ShowService showService;
    private final UsersRepository usersRepository;


    public ZTicketDataGenerator(TicketRepository ticketRepository, ShowRepository showRepository,
                                ShowService showService, UsersRepository usersRepository) {
        this.ticketRepository = ticketRepository;
        this.showRepository = showRepository;
        this.showService = showService;
        this.usersRepository = usersRepository;
    }

    @PostConstruct
    public void generateTickets() {
        if (ticketRepository.findAll().size() > 0) {
            LOGGER.debug("tickets already generated");
        } else {
            List<Ticket> tickets = new LinkedList<>();
            List<Show> shows = showRepository.findAll();
            List<ApplicationUser> costumers = usersRepository.findAllByAdminFalse();
            Faker faker = new Faker();
            for (Show show : shows) {
                List<Seat> seats = showService.getSeatsOfShow(show.getId());
                LOGGER.debug("generating {} ticket entries for show with id {}", seats.size(), show.getId());
                for (Seat seat: seats) {
                    if (seat.isRealSeat()) {
                        Ticket ticket = new Ticket();
                        ticket.setShow(show);
                        ticket.setSeat(seat);
                        ticket.setStatus(VALUES.get(RANDOM.nextInt(SIZE)));
                        if (seat.getRowNr() <= 2) {
                            ticket.setSector('0');
                            ticket.setPrice(40.90f);
                            ticket.setStatus(Ticket.Status.FREE);
                        } else if (seat.getRowNr() <= 4) {
                            ticket.setSector('A');
                            ticket.setPrice(65.80f);
                        } else if (seat.getRowNr() <= 7) {
                            ticket.setSector('B');
                            ticket.setPrice(50.50f);
                        } else {
                            ticket.setSector('C');
                            ticket.setPrice(35.00f);
                        }
                        int month = RANDOM.nextInt(2) + 4;
                        int currentMonth = LocalDate.now().getMonthValue();
                        int day = RANDOM.nextInt(26) + 1;
                        int currentDay = LocalDate.now().getDayOfMonth();
                        if (ticket.getStatus() == Ticket.Status.PURCHASED) {
                            LocalDate dateOfPurchase = LocalDate.of(
                                2020, Month.of(month), month == currentMonth ? (day % currentDay) + 1 : day);
                            ticket.setDateOfPurchase(dateOfPurchase);
                            ticket.setUser(costumers.get(faker.number().numberBetween(0, costumers.size())));
                            ticket.setOrderId(ticketRepository.getNextOrderId());
                        }
                        if (ticket.getStatus() == Ticket.Status.RESERVED) {
                            LocalDate dateOfReservation = LocalDate.of(
                                show.getStartDate().getYear() - 1, Month.of(month), day);
                            ticket.setDateOfReservation(dateOfReservation);
                            ticket.setUser(costumers.get(faker.number().numberBetween(0, costumers.size())));
                            ticket.setOrderId(ticketRepository.getNextOrderId());
                        }
                        tickets.add(ticket);
                    }
                }
            }
            ticketRepository.saveAll(tickets);
        }

    }
}
