package at.ac.tuwien.sepm.groupphase.backend.unittests.service;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.*;
import at.ac.tuwien.sepm.groupphase.backend.service.ShowService;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import at.ac.tuwien.sepm.groupphase.backend.service.impl.SimpleShowService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class ShowServiceTest implements TestData {

    @Mock
    private ShowRepository showRepository;

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private SeatRepository seatRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private UserService userService;

    private ShowService showService;
    private Show show;
    private Ticket ticket1;
    private Ticket ticket2;
    private final List<Ticket> testTickets = new ArrayList<>();
    private Seat seat1;
    private Seat seat2;
    private Seat seat3;
    private final List<Seat> testSeats = new ArrayList<>();

    @BeforeEach
    public void initShowService() {
        showService = new SimpleShowService(showRepository, ticketRepository, seatRepository, eventRepository, locationRepository, usersRepository, userService);
        show = TestData.getTestShow();

        //Tickets
        ticket1 = TestData.getTestTicketWithId(1L);
        ticket1.setStatus(Ticket.Status.RESERVED);
        ticket1.setSector('A');
        ticket1.setPrice(50.90f);
        ticket2 = TestData.getTestTicketWithId(2L);
        ticket2.setSector('B');
        ticket2.setPrice(80.50f);
        ticket1.setShow(show);
        ticket2.setShow(show);
        testTickets.add(ticket1);
        testTickets.add(ticket2);

        //Seats
        seat1 = TestData.getTestSeatWithId(1L);
        seat2 = TestData.getTestSeatWithId(2L);
        seat2.setRowNr(33);
        seat3 = TestData.getTestSeatWithId(3L);
        seat3.setRealSeat(false);

        ticket1.setSeat(seat1);
        ticket2.setSeat(seat2);
        testSeats.add(seat1);
        testSeats.add(seat2);
        testSeats.add(seat3);
        testSeats.forEach(item -> item.setLocation(show.getLocation()));
    }

    @Test
    public void givenOneShow_whenGetSeatsWithTicketOrNull_thenSeatTicketMapWithCorrectValues() {
        //Arrange
        when(showRepository.findById(show.getId())).thenReturn(Optional.ofNullable(show));
        when(ticketRepository.findByShow(show)).thenReturn(testTickets);
        when(seatRepository.findByLocation(show.getLocation())).thenReturn(testSeats);

        //Act
        Map<Seat, Ticket> map = showService.getSeatsWithTicket(show.getId());

        //Assert
        assertThat(map).contains(entry(seat1, ticket1), entry(seat2, ticket2), entry(seat3, null));
    }

    @Test
    public void givenNothing_whenGetSeatsWithTicketOrNull_thenThrowsNotFoundException() {
        //Arrange
        when(showRepository.findById(show.getId())).thenReturn(Optional.empty());

        //Act & Assert
        assertThatExceptionOfType(NotFoundException.class).isThrownBy(
            () -> showService.getSeatsWithTicket(show.getId())
        );
    }

    @Test
    public void givenOneShowAndSomeTickets_whenGetTicketsOfShow_thenListOfCorrectTickets() {
        //Arrange
        when(showRepository.findById(show.getId())).thenReturn(Optional.ofNullable(show));
        when(ticketRepository.findByShow(show)).thenReturn(testTickets);

        //Act
        List<Ticket> tickets = showService.getTicketsOfShow(show.getId());

        //Assert
        assertThat(tickets).containsOnlyElementsOf(testTickets);
    }

    @Test
    public void givenNothing_whenGetTicketsOfShow_thenThrowsNotFoundException() {
        //Arrange
        when(showRepository.findById(show.getId())).thenReturn(Optional.empty());

        //Act & Assert
        assertThatExceptionOfType(NotFoundException.class).isThrownBy(
            () -> showService.getTicketsOfShow(show.getId())
        );
    }

    @Test
    public void givenOneShow_whenGetSeatsOfShow_thenListOfCorrectSeats() {
        //Arrange
        when(showRepository.findById(show.getId())).thenReturn(Optional.ofNullable(show));
        when(seatRepository.findByLocation(show.getLocation())).thenReturn(testSeats);

        //Act
        List<Seat> seats = showService.getSeatsOfShow(show.getId());

        //Assert
        assertThat(seats).containsOnlyElementsOf(testSeats);
    }

    @Test
    public void givenNothing_whenGetSeatsOfShow_thenThrowsNotFoundException() {
        //Arrange
        when(showRepository.findById(show.getId())).thenReturn(Optional.empty());

        //Act & Assert
        assertThatExceptionOfType(NotFoundException.class).isThrownBy(
            () -> showService.getSeatsOfShow(show.getId())
        );
    }

    @Test
    public void givenOneShow_whenFindOne_thenCorrectShow() {
        //Arrange
        when(showRepository.findById(show.getId())).thenReturn(Optional.ofNullable(show));

        //Act
        Show returnedShow = showService.findOne(show.getId());

        //Assert
        assertThat(returnedShow).isEqualTo(show);
    }

    @Test
    public void givenNothing_whenFindOne_thenThrowsNotFoundException() {
        //Arrange
        when(showRepository.findById(show.getId())).thenReturn(Optional.empty());

        //Act & Assert
        assertThatExceptionOfType(NotFoundException.class).isThrownBy(
            () -> showService.findOne(show.getId())
        );
    }

    @Test
    public void givenNothing_whenSaveShowWithValidProperties_thenReturnsSavedShow() {
        //Arrange
        Event event = TestData.getTestEvent();
        SaveShow saveShow = new SaveShow();
        show.setEndTime(show.getStartTime().plusMinutes(event.getDuration()));
        show.setEvent(event);
        saveShow.setShow(show);
        saveShow.setTickets(testTickets);
        when(showRepository.save(show)).thenReturn(show);
        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));
        when(seatRepository.findByLocation(any())).thenReturn(testSeats);
        when(locationRepository.findById(anyLong())).thenReturn(Optional.of(show.getLocation()));

        //Act
        Show returnedShow = showService.saveShow(saveShow);

        //Assert
        assertThat(returnedShow).isEqualTo(show);
    }

    @Test
    public void givenNothing_whenSaveShowWithInvalidPropertiesNoTickets_thenThrowsValidationException() {
        //Arrange
        Event event = TestData.getTestEvent();
        SaveShow saveShow = new SaveShow();
        show.setEndTime(show.getStartTime().plusMinutes(event.getDuration()));
        show.setEvent(event);
        saveShow.setShow(show);
        saveShow.setTickets(null);

        //Act & Assert
        assertThatExceptionOfType(ValidationException.class).isThrownBy(
            () -> showService.saveShow(saveShow)
        );
    }

    @Test
    public void givenNothing_whenSaveShowWithInvalidPropertiesTicketsNotForEverySeat_thenThrowsValidationException() {
        //Arrange
        Event event = TestData.getTestEvent();
        SaveShow saveShow = new SaveShow();
        show.setEndTime(show.getStartTime().plusMinutes(event.getDuration()));
        show.setEvent(event);
        saveShow.setShow(show);
        saveShow.setTickets(testTickets);
        saveShow.getTickets().remove(0);
        when(seatRepository.findByLocation(any())).thenReturn(testSeats);
        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));
        when(locationRepository.findById(anyLong())).thenReturn(Optional.of(show.getLocation()));

        //Act & Assert
        assertThatExceptionOfType(ValidationException.class).isThrownBy(
            () -> showService.saveShow(saveShow)
        );
    }

    @Test
    public void givenNothing_whenSaveShowWithInvalidPropertiesInvalidTickets_thenThrowsValidationException() {
        //Arrange
        Event event = TestData.getTestEvent();
        SaveShow saveShow = new SaveShow();
        show.setEndTime(show.getStartTime().plusMinutes(event.getDuration()));
        show.setEvent(event);
        saveShow.setShow(show);
        ticket1.setSector('A');
        ticket1.setPrice(50.90f);
        ticket2.setSector('A');
        ticket2.setPrice(80.50f);
        saveShow.setTickets(testTickets);
        when(seatRepository.findByLocation(any())).thenReturn(testSeats);
        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));
        when(locationRepository.findById(anyLong())).thenReturn(Optional.of(show.getLocation()));

        //Act & Assert
        assertThatExceptionOfType(ValidationException.class).isThrownBy(
            () -> showService.saveShow(saveShow)
        );
    }

    @Test
    public void givenOneShow_whenPurchaseFreeTickets_thenNewOrder() {
        //Arrange
        when(showRepository.findById(show.getId())).thenReturn(Optional.ofNullable(show));
        ticket1.setStatus(Ticket.Status.FREE);
        ticket2.setStatus(Ticket.Status.FREE);
        when(ticketRepository.findByShow(show)).thenReturn(testTickets);
        when(ticketRepository.getNextOrderId()).thenReturn(2L);
        when(ticketRepository.save(any())).thenReturn(TestData.getTestTicket());
        when(usersRepository.findByEmail(DEFAULT_USER)).thenReturn(TestData.getTestUser());

        List<Long> seatIds = new ArrayList<>();
        seatIds.add(seat1.getId());
        seatIds.add(seat2.getId());

        //Act
        Long orderId = showService.orderTickets(seatIds, show.getId(), DEFAULT_USER, "PURCHASE")[0];

        //Assert
        assertThat(orderId).isEqualTo(2L);
    }

    @Test
    public void givenOneShow_whenPurchaseUnavailableTickets_thenThrowsValidationException() {
        //Arrange
        when(showRepository.findById(show.getId())).thenReturn(Optional.ofNullable(show));
        ticket1.setStatus(Ticket.Status.RESERVED);
        ticket2.setStatus(Ticket.Status.FREE);
        when(ticketRepository.findByShow(show)).thenReturn(testTickets);
        when(ticketRepository.getNextOrderId()).thenReturn(2L);
        when(usersRepository.findByEmail(DEFAULT_USER)).thenReturn(TestData.getTestUser());

        List<Long> seatIds = new ArrayList<>();
        seatIds.add(seat1.getId());
        seatIds.add(seat2.getId());

        //Act & Assert
        assertThatExceptionOfType(ValidationException.class).isThrownBy(
            () -> showService.orderTickets(seatIds, show.getId(), DEFAULT_USER, "PURCHASE")
        );
    }

    @Test
    public void givenOneShow_whenReserveFreeTickets_thenNewOrder() {
        //Arrange
        when(showRepository.findById(show.getId())).thenReturn(Optional.ofNullable(show));
        ticket1.setStatus(Ticket.Status.FREE);
        ticket2.setStatus(Ticket.Status.FREE);
        when(ticketRepository.findByShow(show)).thenReturn(testTickets);
        when(ticketRepository.getNextOrderId()).thenReturn(3L);
        when(ticketRepository.save(any())).thenReturn(TestData.getTestTicket());
        when(usersRepository.findByEmail(DEFAULT_USER)).thenReturn(TestData.getTestUser());

        List<Long> seatIds = new ArrayList<>();
        seatIds.add(seat1.getId());
        seatIds.add(seat2.getId());

        //Act
        Long orderId = showService.orderTickets(seatIds, show.getId(), DEFAULT_USER, "RESERVE")[0];

        //Assert
        assertThat(orderId).isEqualTo(3L);
    }

    @Test
    public void givenOneShow_whenReserveUnavailableTickets_thenThrowsValidationException() {
        //Arrange
        when(showRepository.findById(show.getId())).thenReturn(Optional.ofNullable(show));
        ticket1.setStatus(Ticket.Status.FREE);
        ticket2.setStatus(Ticket.Status.PURCHASED);
        when(ticketRepository.findByShow(show)).thenReturn(testTickets);
        when(ticketRepository.getNextOrderId()).thenReturn(2L);
        when(usersRepository.findByEmail(DEFAULT_USER)).thenReturn(TestData.getTestUser());

        List<Long> seatIds = new ArrayList<>();
        seatIds.add(seat1.getId());
        seatIds.add(seat2.getId());

        //Act & Assert
        assertThatExceptionOfType(ValidationException.class).isThrownBy(
            () -> showService.orderTickets(seatIds, show.getId(), DEFAULT_USER, "RESERVE")
        );
    }

    @Test
    public void givenNoTickets_forUserByOrderId_thenThrowValidationException() {
        //Arrange
        when(usersRepository.findByEmail(DEFAULT_USER)).thenReturn(TestData.getTestUser());

        //Act & Assert
        assertThat(showService.getTicketsForUserByOrderId(1L, DEFAULT_USER).size()).isEqualTo(0);
    }

    @Test
    public void givenTickets_forUserByOrderId_thenReturnTickets() {
        //Arrange
        when(usersRepository.findByEmail(DEFAULT_USER)).thenReturn(TestData.getTestUser());
        ApplicationUser user = usersRepository.findByEmail(DEFAULT_USER);

        ticket1.setOrderId(1L);
        ticket1.setUser(user);

        List<Ticket> tickets = new ArrayList<>();
        tickets.add(ticket1);

        //Act & Assert
        when(ticketRepository.getTicketsForUserByOrderId(user.getId(), 1L)).thenReturn(tickets);

        assertThat(showService.getTicketsForUserByOrderId(1L, user.getEmail()).size()).isEqualTo(1);
    }

    @Test
    public void cancelOneTicket_whenTicketDoesNotExist_thenThrowValidationException() {
        //Arrange


        when(usersRepository.findByEmail(DEFAULT_USER)).thenReturn(TestData.getTestUser());
        ApplicationUser user = usersRepository.findByEmail(DEFAULT_USER);

        ticket1.setOrderId(1L);
        ticket1.setUser(user);

        List<Long> ids = new ArrayList<>();
        ids.add(5L);

        when(ticketRepository.findById(5L)).thenReturn(Optional.empty());

        //Act & Assert
        assertThatExceptionOfType(NotFoundException.class).isThrownBy(
            () -> showService.cancelTickets(ids, user.getEmail())
        );
    }

    @Test
    public void cancelTwoTickets_whenTicketsValid_thenCancelTickets() {
        //Arrange
        when(usersRepository.findByEmail(DEFAULT_USER)).thenReturn(TestData.getTestUser());
        ApplicationUser user = usersRepository.findByEmail(DEFAULT_USER);

        ticket1.setOrderId(1L);
        ticket1.setUser(user);
        ticket2.setOrderId(1L);
        ticket2.setUser(user);
        ticket1.setStatus(Ticket.Status.RESERVED);
        ticket2.setStatus(Ticket.Status.PURCHASED);
        ticket1.setOrderId(1L);
        ticket2.setOrderId(1L);

        List<Long> ids = new ArrayList<>();
        ids.add(ticket1.getId());
        ids.add(ticket2.getId());

        when(ticketRepository.findById(1L)).thenReturn(Optional.ofNullable(ticket1));
        when(ticketRepository.findById(2L)).thenReturn(Optional.ofNullable(ticket2));

        showService.cancelTickets(ids, user.getEmail());
        //Act & Assert
        assertThat(ticket1.getStatus()).isEqualByComparingTo(Ticket.Status.FREE);
        assertThat(ticket2.getStatus()).isEqualByComparingTo(Ticket.Status.FREE);
        assertThat(ticket1.getUser()).isEqualTo(null);
        assertThat(ticket2.getUser()).isEqualTo(null);
        assertThat(ticket1.getOrderId()).isEqualTo(null);


    }

    @Test
    public void generatePDF_forValidPurchasedTickets_thenGeneratePDF() throws Exception {
        //Arrange
        when(usersRepository.findByEmail(DEFAULT_USER)).thenReturn(TestData.getTestUser());
        ApplicationUser user = usersRepository.findByEmail(DEFAULT_USER);

        ticket1.setOrderId(1L);
        ticket1.setUser(user);


        List<Ticket> tickets = new ArrayList<>();
        tickets.add(ticket1);

        //Act & Assert
        when(ticketRepository.getTicketsForUserByOrderId(user.getId(), 1L)).thenReturn(tickets);

        ResponseEntity<InputStreamResource> responseEntity = showService.ticketPurchaseReport(1L, user.getEmail());

        assertThat(responseEntity.getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
    }

    @Test
    public void generatePDF_forNotValidPurchasedTicketId_thenGeneratePDF() {
        //Arrange
        when(usersRepository.findByEmail(DEFAULT_USER)).thenReturn(TestData.getTestUser());
        ApplicationUser user = usersRepository.findByEmail(DEFAULT_USER);

        List<Ticket> tickets = new ArrayList<>();

        when(ticketRepository.getTicketsForUserByOrderId(user.getId(), 5L)).thenReturn(tickets);

        //Act & Assert
        assertThatExceptionOfType(ValidationException.class).isThrownBy(
            () -> showService.ticketPurchaseReport(5L, user.getEmail())
        );
    }

    @Test
    public void generatePDF_forValidCanceledTickets_thenGeneratePDF() {
        //Arrange
        when(usersRepository.findByEmail(DEFAULT_USER)).thenReturn(TestData.getTestUser());
        ApplicationUser user = usersRepository.findByEmail(DEFAULT_USER);

        ticket1.setOrderId(1L);
        ticket1.setUser(user);

        List<Long> ids = new ArrayList<>();
        ids.add(ticket1.getId());

        List<Ticket> tickets = new ArrayList<>();
        tickets.add(ticket1);

        when(ticketRepository.findById(1L)).thenReturn(Optional.ofNullable(ticket1));
        when(ticketRepository.getTicketsForUserByOrderId(user.getId(), 1L)).thenReturn(tickets);

        //Act & Assert


        ResponseEntity<InputStreamResource> responseEntity = showService.ticketCancelReport(1L, ids, user.getEmail(), 0L);

        assertThat(responseEntity.getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
    }


    @Test
    public void generatePDF_forNotValidCanceledTicketId_thenGeneratePDF() {
        //Arrange
        when(usersRepository.findByEmail(DEFAULT_USER)).thenReturn(TestData.getTestUser());
        ApplicationUser user = usersRepository.findByEmail(DEFAULT_USER);

        List<Long> ids = new ArrayList<>();
        ids.add(5L);

        when(ticketRepository.findById(5L)).thenReturn(Optional.empty());

        //Act & Assert
        assertThatExceptionOfType(NotFoundException.class).isThrownBy(
            () -> showService.ticketCancelReport(1L, ids, user.getEmail(), 0L)
        );
    }

}
