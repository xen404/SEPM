package at.ac.tuwien.sepm.groupphase.backend.datagenerator;

import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import at.ac.tuwien.sepm.groupphase.backend.entity.Merchandise;
import at.ac.tuwien.sepm.groupphase.backend.repository.EventRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.MerchandiseRepository;
import com.github.javafaker.Faker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.lang.invoke.MethodHandles;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Profile("generateData")
@Component
public class MerchandiseDataGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final MerchandiseRepository merchandiseRepository;
    private final EventRepository eventRepository;
    private static final int NUMBER_OF_MERCHANDISE_TO_GENERATE = 30;
    private static String[] type = new String[]{"Hoodie", "Tshirt", "Poster", "Autograph"};

    public MerchandiseDataGenerator(MerchandiseRepository merchandiseRepository, EventRepository eventRepository) {
        this.merchandiseRepository = merchandiseRepository;
        this.eventRepository = eventRepository;
    }

    @PostConstruct
    private void generateMerchandise() {
        Faker faker = new Faker();
        if (merchandiseRepository.findAll().size() > 0) {
            LOGGER.debug("merchandise already generated");
        } else {
            LOGGER.debug("generating {} merchandise items", NUMBER_OF_MERCHANDISE_TO_GENERATE);
            for (int i = 1; i <= NUMBER_OF_MERCHANDISE_TO_GENERATE; i++) {
                int typeIndex = faker.number().numberBetween(0, type.length);


                Event event = new Event();
                Long randomEventId = (long) ThreadLocalRandom.current().nextInt(1, eventRepository.findAll().size() + 1);
                Optional<Event> eventOptional = eventRepository.findById(randomEventId);
                if (eventOptional.isPresent()) {
                    event = eventOptional.get();
                }


                Merchandise merchandise = Merchandise.MerchandiseBuilder.aMerchandise()
                    .withId((long) i)
                    .withTitle(event.getTitle() + " " + type[typeIndex])
                    .withBonusPoints(faker.number().numberBetween(10, 200))
                    .withPrice(faker.number().randomDouble(2, 10, 75))
                    .withDescription(faker.lorem().fixedString(150))
                    .withBonus(i%2 == 0)
                    .withEvent(event)
                    .withPicture(File.separator + "src/main/resources/images/" + type[typeIndex] + ".jpg")
                    .build();
                merchandiseRepository.save(merchandise);

            }
        }
    }


}
