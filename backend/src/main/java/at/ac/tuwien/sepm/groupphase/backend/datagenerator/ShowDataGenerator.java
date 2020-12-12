package at.ac.tuwien.sepm.groupphase.backend.datagenerator;

import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import at.ac.tuwien.sepm.groupphase.backend.entity.Location;
import at.ac.tuwien.sepm.groupphase.backend.entity.Show;
import at.ac.tuwien.sepm.groupphase.backend.repository.EventRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.LocationRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.ShowRepository;
import com.github.javafaker.Faker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.invoke.MethodHandles;
import java.time.*;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Profile("generateData")
@Component
public class ShowDataGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final ShowRepository showRepository;
    private final EventRepository eventRepository;
    private final LocationRepository locationRepository;
    private List<Show> savedShows = new LinkedList<>();

    public ShowDataGenerator(ShowRepository showRepository, EventRepository eventRepository,
                             LocationRepository locationRepository) {
        this.showRepository = showRepository;
        this.eventRepository = eventRepository;
        this.locationRepository = locationRepository;
    }

    @PostConstruct
    public void generateShows() {
        if (showRepository.findAll().size() > 0) {
            LOGGER.debug("shows already generated");
        } else {
            LOGGER.debug("generating show entries");
            Faker faker = new Faker();
            int numberOfEvents = eventRepository.findAll().size();
            int numberOfLocations = locationRepository.findAll().size();
            for (long eventId = 1L; eventId <= numberOfEvents; eventId++) {
                Event event = eventRepository.findById(eventId).get();
                int showNumber = faker.number().numberBetween(2, 11);

                int duration = faker.number().numberBetween(1, 5);
                event.setDuration(duration * 60);
                eventRepository.save(event);

                for (int i = 0; i < showNumber; i++) {
                    Show show = new Show();
                    String description = faker.lorem().fixedString(250);
                    Long randomLocationId = (long) faker.number().numberBetween(1, numberOfLocations + 1);
                    Location location = locationRepository.findById(randomLocationId).get();

                    LocalDate randomDate;
                    if (i == 0) {
                        randomDate = event.getStartDate();
                    } else if (i == showNumber - 1) {
                        randomDate = event.getEndDate();
                    } else {
                        Date random = faker.date().between(toDate(
                            event.getStartDate().plusDays(1L)), toDate(event.getEndDate().minusDays(1L)));
                        randomDate = toLocalDate(random);
                    }
                    int begin = faker.number().numberBetween(2, 9);
                    LocalTime randomTimeStart = LocalTime.NOON.plusHours(begin);
                    LocalTime randomTimeEnd = randomTimeStart.plusHours(duration);

                    show.setTitle(event.getTitle() + " " + randomDate);
                    show.setLocation(location);
                    show.setStartTime(randomTimeStart);
                    show.setEndTime(randomTimeEnd);
                    show.setDuration(duration * 60);
                    show.setStartDate(randomDate);
                    show.setEndDate(randomDate);
                    show.setDescription(description);
                    show.setEvent(event);

                    LOGGER.debug("saving show {}", show);
                    savedShows.add(show);
                }
            }
            showRepository.saveAll(savedShows);
        }
    }

    private LocalDate toLocalDate(Date date) {
        Instant instant = Instant.ofEpochMilli(date.getTime());
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        return localDateTime.toLocalDate();
    }

    private Date toDate(LocalDate localDate) {
        ZoneId defaultZoneId = ZoneId.systemDefault();
        return Date.from(localDate.atStartOfDay(defaultZoneId).toInstant());
    }
}
