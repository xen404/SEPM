package at.ac.tuwien.sepm.groupphase.backend.datagenerator;

import at.ac.tuwien.sepm.groupphase.backend.entity.Artist;
import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import at.ac.tuwien.sepm.groupphase.backend.repository.ArtistRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.EventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

import com.github.javafaker.Faker;


@Profile("generateData")
@Component
public class ArtistDataGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final ArtistRepository artistRepository;
    private final EventRepository eventRepository;

    public ArtistDataGenerator(ArtistRepository artistRepository, EventRepository eventRepository) {
        this.artistRepository = artistRepository;
        this.eventRepository = eventRepository;
    }

    @PostConstruct
    private void generateMessage() {
        if (artistRepository.findAll().size() > 0) {
            LOGGER.debug("artist already generated");
        } else {
            LOGGER.debug("generating artist entries");
            Faker faker = new Faker();
            int numberOfEvents = eventRepository.findAll().size();

            for (Long eventId = 1L; eventId <= numberOfEvents; eventId++) {
                int artistNumber = faker.number().numberBetween(1, 4);
                for (int i = 0; i < artistNumber; i++) {
                    List<Event> events = new ArrayList<>();
                    events.add(eventRepository.findById(eventId).get());
                    String name = "";
                    switch (events.get(0).getCategory().name()) {
                        case "CABARET":
                            if (i == 0) {
                                String[] namePart = events.get(0).getTitle().split("-");
                                name = namePart[0];
                            } else {
                                name = faker.name().fullName();
                            }
                            break;
                        case "CINEMA":
                            name = faker.name().fullName();
                            break;
                        case "CIRCUS":
                            name = faker.name().fullName();
                            break;
                        case "CONCERT":
                            if (i == 0) {
                                String[] namePart = events.get(0).getTitle().split("-");
                                name = namePart[0];
                            } else {
                                name = faker.rockBand().name();
                            }
                            break;
                        case "MUSICAL":
                            name = faker.name().fullName();
                            break;
                        case "OPERA":
                            name = faker.name().fullName();
                            break;
                        case "THEATRE":
                            name = faker.name().fullName();
                            break;
                    }

                    if (artistRepository.findByNameContainingIgnoreCase(name).size() <= 0) {
                        Artist artist = Artist.ArtistBuilder.anArtist()
                            .withName(name)
                            .withEvents(events)
                            .build();
                        LOGGER.debug("saving artist {}", artist);
                        artistRepository.save(artist);
                    }
                }
            }

            /*Artist artist = Artist.ArtistBuilder.anArtist()
                .withName("Bon Iver")
                .withEvents(new ArrayList<>() {
                    {
                        add(eventRepository.findById(1L).get());
                    }
                })
                .build();
            artistRepository.save(artist);*/
        }
    }
}
