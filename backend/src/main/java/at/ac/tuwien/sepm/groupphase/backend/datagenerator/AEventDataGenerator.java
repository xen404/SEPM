package at.ac.tuwien.sepm.groupphase.backend.datagenerator;

import at.ac.tuwien.sepm.groupphase.backend.entity.Artist;
import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import at.ac.tuwien.sepm.groupphase.backend.entity.Show;
import at.ac.tuwien.sepm.groupphase.backend.repository.EventRepository;
import com.github.javafaker.Faker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import util.EventCategory;

import javax.annotation.PostConstruct;
import java.io.File;
import java.lang.invoke.MethodHandles;
import java.time.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Profile("generateData")
@Component
public class AEventDataGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final int NUMBER_OF_EVENTS_TO_GENERATE_PER_CATEGORY = 35;
    private static final List<Show> TEST_EVENT_SHOWS = new ArrayList<>();
    private static final List<Artist> TEST_EVENT_ARTISTS = new ArrayList<>();
    private String[] cabaretTitles = new String[]{" - stand up ", " - improv", " - and friends", " - live"};
    private String[] circusTitles = new String[]{"The fantastic ", "The magnificent ", "The amazing ",
        "The wonderful "};
    private String[] concertTitles = new String[]{" - live", " - on tour", " - open air ", " - best of"};
    private String[] musicalTitles = new String[]{" - the musical", " - on ice", " - the cult musical",
        " - the broadway musical"};
    private String[] operaTitles = new String[]{" - the opera", " - in french", " - in english", " - classic"};
    private String[] theaterTitles = new String[]{" - a comedy", " - a tragedy", " - the puppet show", " - the original"};
    private List<Event> savedEvents = new LinkedList<>();

    private final EventRepository eventRepository;

    public AEventDataGenerator(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @PostConstruct
    public void generateEvents() {
        if (eventRepository.findAll().size() > 0) {
            LOGGER.debug("Events are already generated");
        } else {
            LOGGER.debug("generating {} event entries for each category", NUMBER_OF_EVENTS_TO_GENERATE_PER_CATEGORY);
            for (EventCategory eventCategory : EventCategory.values()) {
                Faker faker = new Faker();
                int size = faker.number().numberBetween(NUMBER_OF_EVENTS_TO_GENERATE_PER_CATEGORY - 6,
                    NUMBER_OF_EVENTS_TO_GENERATE_PER_CATEGORY);
                generate(eventCategory, size);
            }
        }
    }

    private void generate(EventCategory eventCategory, int size) {
        for (int i = 0; i < size; i++) {
            Faker faker = new Faker();
            String description = faker.lorem().fixedString(400);
            String title;
            switch (eventCategory.name()) {
                case "CABARET":
                    title = faker.name().fullName() + cabaretTitles[i % 4];
                    break;
                case "CINEMA":
                    title = faker.book().title();
                    break;
                case "CIRCUS":
                    title = circusTitles[i % 4] + faker.animal().name() + " circus";
                    break;
                case "CONCERT":
                    title = faker.rockBand().name() + concertTitles[i % 4];
                    break;
                case "MUSICAL":
                    title = faker.book().title() + musicalTitles[i % 4];
                    break;
                case "OPERA":
                    title = faker.artist().name() + operaTitles[i % 4];
                    break;
                case "THEATRE":
                    title = faker.book().title() + theaterTitles[i % 4];
                    break;
                default:
                    title = "title";
            }

            Date start = faker.date().future(1000, TimeUnit.DAYS);
            LocalDate startDate = toLocalDate(start);
            LocalDate endDate = startDate.plusDays(faker.number().numberBetween(2, 200));

            Event event = new Event();

            event.setTitle(title);
            event.setDescription(description);
            event.setCategory(eventCategory);
            event.setDuration(0);
            event.setImage(File.separator + "src/main/resources/images/" + eventCategory.name().toLowerCase()
                + i % 8 + ".jpg");
            event.setShows(TEST_EVENT_SHOWS);
            event.setArtists(TEST_EVENT_ARTISTS);
            event.setStartDate(startDate);
            event.setEndDate(endDate);
            LOGGER.debug("saving event {}", event);
            savedEvents.add(event);
        }
        eventRepository.saveAll(savedEvents);
    }

    private LocalDate toLocalDate(Date date) {
        Instant instant = Instant.ofEpochMilli(date.getTime());
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        return localDateTime.toLocalDate();
    }

}
