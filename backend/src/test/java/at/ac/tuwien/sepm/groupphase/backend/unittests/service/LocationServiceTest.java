package at.ac.tuwien.sepm.groupphase.backend.unittests.service;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.entity.Location;
import at.ac.tuwien.sepm.groupphase.backend.entity.Seat;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.LocationRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.SeatRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.LocationService;
import at.ac.tuwien.sepm.groupphase.backend.service.impl.SimpleLocationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class LocationServiceTest implements TestData {

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private SeatRepository seatRepository;

    private LocationService locationService;
    private Location location;
    private List<Seat> testSeats;

    @BeforeEach
    public void initShowService() {
        locationService = new SimpleLocationService(locationRepository, seatRepository);
        location = TestData.getTestLocation();
        location.setShows(null);
        location.setSeats(null);
        Seat seat1 = TestData.getTestSeatWithId(1L);
        seat1.setLocation(location);
        Seat seat2 = TestData.getTestSeatWithId(2L);
        seat2.setLocation(location);
        testSeats = new ArrayList<>();
        testSeats.add(seat1);
        testSeats.add(seat2);
    }

    @Test
    public void givenSomeSeats_whenGetSeats_thenListOfCorrectSeats() {
        //Arrange
        when(locationRepository.findById(location.getId())).thenReturn(Optional.of(location));
        when(seatRepository.findByLocation(location)).thenReturn(testSeats);

        //Act
        List<Seat> seats = locationService.getSeats(location.getId());

        //Assert
        assertThat(seats).containsOnlyElementsOf(testSeats);
    }

    @Test
    public void givenNothing_whenGetSeats_thenThrowsNotFoundException() {
        //Arrange
        when(locationRepository.findById(location.getId())).thenReturn(Optional.empty());

        //Act & Assert
        assertThatExceptionOfType(NotFoundException.class).isThrownBy(
            () -> locationService.getSeats(location.getId())
        );
    }

}
