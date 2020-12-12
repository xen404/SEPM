package at.ac.tuwien.sepm.groupphase.backend.unittests.repository;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import at.ac.tuwien.sepm.groupphase.backend.repository.TopEventRepository;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import util.EventCategory;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@ActiveProfiles("test")
public class TopTenEventsRepositoryTest {

    @Autowired
    private TopEventRepository topEventRepository;

    @Autowired
    private TestEntityManager entityManager;


    @Test
    public void givenCategoryAndDates_whenFindTopEventByCategory_thenFindAccordingEvents() {
        //Arrange
        EventCategory TEST_EVENT_CATEGORY = EventCategory.MUSICAL;
        int category = TEST_EVENT_CATEGORY.ordinal();

        Location location = TestData.getTestLocationWithId(null);
        location.setShows(new ArrayList<>());
        location.setSeats(new ArrayList<>());
        location = entityManager.persist(location);
        Seat seat1 = TestData.getTestSeatWithId(null);
        seat1.setLocation(location);
        seat1 = entityManager.persist(seat1);
        Seat seat2 = TestData.getTestSeatWithId(null);
        seat2.setLocation(location);
        seat2 = entityManager.persist(seat2);

        Event event1 = TestData.getTestEventWithId(null);
        event1.setTitle("Musical event 1");
        event1.setCategory(TEST_EVENT_CATEGORY);
        event1.setShows(new ArrayList<>());
        event1.setArtists(new ArrayList<>());
        event1 = entityManager.persist(event1);
        Show show1 = TestData.getTestShowWithId(null);
        show1.setEvent(event1);
        show1.setLocation(location);
        show1 = entityManager.persist(show1);
        Ticket ticket1 = TestData.getTestTicketWithId(null);
        ticket1.setOrderId(1L);
        ticket1.setStatus(Ticket.Status.PURCHASED);
        ticket1.setDateOfPurchase(LocalDate.of(2020, 3, 15));
        ticket1.setSeat(seat1);
        ticket1.setShow(show1);
        ticket1 = entityManager.persist(ticket1);
        Ticket ticket2 = TestData.getTestTicketWithId(null);
        ticket2.setOrderId(1L);
        ticket2.setStatus(Ticket.Status.PURCHASED);
        ticket2.setDateOfPurchase(LocalDate.of(2020, 3, 15));
        ticket2.setSeat(seat2);
        ticket2.setShow(show1);
        ticket2 = entityManager.persist(ticket2);

        Event event2 = TestData.getTestEventWithId(null);
        event2.setTitle("Musical event 2");
        event2.setCategory(TEST_EVENT_CATEGORY);
        event2.setShows(new ArrayList<>());
        event2.setArtists(new ArrayList<>());
        event2 = entityManager.persist(event2);
        Show show2 = TestData.getTestShowWithId(null);
        show2.setEvent(event2);
        show2.setLocation(location);
        show2 = entityManager.persist(show2);
        Ticket ticket3 = TestData.getTestTicketWithId(null);
        ticket3.setOrderId(2L);
        ticket3.setStatus(Ticket.Status.PURCHASED);
        ticket3.setDateOfPurchase(LocalDate.of(2020, 1, 12));
        ticket3.setSeat(seat1);
        ticket3.setShow(show2);
        ticket3 = entityManager.persist(ticket3);

        Event event3 = TestData.getTestEventWithId(null);
        event3.setTitle("Musical event 3");
        event3.setCategory(TEST_EVENT_CATEGORY);
        event3.setShows(new ArrayList<>());
        event3.setArtists(new ArrayList<>());
        event3 = entityManager.persist(event3);
        Show show3 = TestData.getTestShowWithId(null);
        show3.setEvent(event3);
        show3.setLocation(location);
        show3 = entityManager.persist(show3);
        Ticket ticket4 = TestData.getTestTicketWithId(null);
        ticket4.setOrderId(3L);
        ticket4.setStatus(Ticket.Status.PURCHASED);
        ticket4.setDateOfPurchase(LocalDate.of(2020, 3, 10));
        ticket4.setSeat(seat1);
        ticket4.setShow(show3);
        ticket4 = entityManager.persist(ticket4);

        LocalDate start = LocalDate.of(2020, 3, 1);
        LocalDate end = LocalDate.of(2020, 3, 31);

        //Act
        List<SoldTicketsPerEvent> soldTicketsPerEvents = topEventRepository.findTopTenByCategory(category, Date.valueOf(start), Date.valueOf(end));

        assertThat(soldTicketsPerEvents.size()).isEqualTo(2);
        assertThat(soldTicketsPerEvents.get(0).getId()).isEqualTo(event1.getId());
        assertThat(soldTicketsPerEvents.get(1).getId()).isEqualTo(event3.getId());
    }
}
