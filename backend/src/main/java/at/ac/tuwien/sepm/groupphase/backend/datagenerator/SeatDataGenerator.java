package at.ac.tuwien.sepm.groupphase.backend.datagenerator;

import at.ac.tuwien.sepm.groupphase.backend.entity.Location;
import at.ac.tuwien.sepm.groupphase.backend.entity.Seat;
import at.ac.tuwien.sepm.groupphase.backend.entity.Show;
import at.ac.tuwien.sepm.groupphase.backend.repository.LocationRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.SeatRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Random;

@Profile("generateData")
@Component
public class SeatDataGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final int NUMBER_OF_SEATS_TO_GENERATE = 5;
    private static final int NUMBER_OF_ROWS = 7;
    private static final int NUMBER_OF_SEATS_PER_ROW = NUMBER_OF_SEATS_TO_GENERATE / NUMBER_OF_ROWS;
    private static final int MAX_NUMBER_OF_ROWS = 20;
    private static final int MIN_NUMBER_OF_ROWS = 4;
    private static final int MAX_NUMBER_OF_SEATS_PER_ROW = 20;
    private static final int MIN_NUMBER_OF_SEATS_PER_ROW = 5;
    private static final Random RANDOM = new Random();

    private final SeatRepository seatRepository;
    private final LocationRepository locationRepository;

    public SeatDataGenerator(SeatRepository seatRepository, LocationRepository locationRepository) {
        this.seatRepository = seatRepository;
        this.locationRepository = locationRepository;
    }

    @PostConstruct
    public void generateSeats() {
        if (seatRepository.findAll().size() > 0) {
            LOGGER.debug("seats already generated");
        } else {
            List<Location> locations = locationRepository.findAll();
            for (Location location : locations) {
                int numberOfRows;
                int numberOfSeatsPerRow;
                // generate only few seats for the one location
                if (location.getId() == 1) {
                    numberOfRows = 2;
                    numberOfSeatsPerRow = 6;
                } else {
                    numberOfRows = RANDOM.nextInt((MAX_NUMBER_OF_ROWS - MIN_NUMBER_OF_ROWS) + 1) + MIN_NUMBER_OF_ROWS;
                    numberOfSeatsPerRow = RANDOM.nextInt((MAX_NUMBER_OF_SEATS_PER_ROW - MIN_NUMBER_OF_SEATS_PER_ROW) + 1) + MIN_NUMBER_OF_SEATS_PER_ROW;
                }
                LOGGER.debug("generating {} seat entries for location with id {}", numberOfRows*numberOfSeatsPerRow, location.getId());
                for (int i = 0; i < numberOfRows; i++) {
                    for (int j = 0; j < numberOfSeatsPerRow; j++) {
                        Seat seat = new Seat();
                        seat.setLocation(location);
                        seat.setRowNr(i+1);
                        seat.setSeatNr(j+1);
                        seat.setRealSeat(true);
                        if (i > numberOfRows/4 && i*j%12==0) seat.setRealSeat(RANDOM.nextBoolean());
                        LOGGER.debug("saving seat {}", seat);
                        seatRepository.save(seat);
                    }
                }
            }
        }
    }
}
