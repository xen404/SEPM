package at.ac.tuwien.sepm.groupphase.backend.unittests.service;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import at.ac.tuwien.sepm.groupphase.backend.entity.Seat;
import at.ac.tuwien.sepm.groupphase.backend.entity.Show;
import at.ac.tuwien.sepm.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.EventRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.TopEventRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.EventService;
import at.ac.tuwien.sepm.groupphase.backend.service.impl.SimpleEventService;
import at.ac.tuwien.sepm.groupphase.backend.service.impl.SimpleShowService;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class EventServiceTest implements TestData {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private TopEventRepository topEventRepository;

    private EventService eventService;
    private Event event;

    @BeforeEach
    public void initShowService() {
        eventService = new SimpleEventService(eventRepository, topEventRepository);
        event = TestData.getTestEvent();
        event.setArtists(new ArrayList<>());
        event.setShows(new ArrayList<>());
    }

    @Test
    public void givenOneEvent_whenFindOne_thenCorrectEvent() {
        //Arrange
        event.setShows(TEST_EVENT_SHOWS);
        when(eventRepository.findById(event.getId())).thenReturn(Optional.ofNullable(event));

        //Act
        Event returnedEvent = eventService.findOne(event.getId());

        //Assert
        assertThat(returnedEvent).isEqualTo(event);
    }

    @Test
    public void givenNothing_whenFindOne_thenThrowsNotFoundException() {
        //Arrange
        when(eventRepository.findById(event.getId())).thenReturn(Optional.empty());

        //Act & Assert
        assertThatExceptionOfType(NotFoundException.class).isThrownBy(
            () -> eventService.findOne(event.getId())
        );
    }

    @Test
    public void givenTwoEvents_whenFindAll_thenCorrectEvents() {
        //Arrange
        Event event1 = TestData.getTestEventWithId(1L);
        event1.setArtists(new ArrayList<>());
        event1.setShows(new ArrayList<>());
        Event event2 = TestData.getTestEventWithId(2L);
        event2.setArtists(new ArrayList<>());
        event2.setShows(new ArrayList<>());
        List<Event> events = new ArrayList<>();
        events.add(event1);
        events.add(event2);
        when(eventRepository.findAll()).thenReturn(events);

        //Act
        List<Event> returnedEvents = eventService.findAll();

        //Assert
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(returnedEvents.size()).isEqualTo(2);
        softly.assertThat(returnedEvents).containsOnlyElementsOf(events);
        softly.assertAll();;
    }

    @Test
    public void givenNothing_whenFindAll_thenReturnsEmptyList() {
        //Arrange
        when(eventRepository.findAll()).thenReturn(new ArrayList<>());

        //Act
        List<Event> returnedEvents = eventService.findAll();

        //Act & Assert
        assertThat(returnedEvents.size()).isEqualTo(0);
    }

}
