package at.ac.tuwien.sepm.groupphase.backend.datagenerator;

import at.ac.tuwien.sepm.groupphase.backend.entity.SoldTicketsPerEvent;
import at.ac.tuwien.sepm.groupphase.backend.repository.TopEventRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.EventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.invoke.MethodHandles;
import java.util.Random;


@Profile("generateData")
@Component
public class TopEventsDataGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final int NUMBER_OF_EVENTS_TO_GENERATE = 13;
    private static final String TEST_EVENT_TITLE = "Title";
    private static final String TEST_EVENT_CATEGORY = "CONCERT";

    private final TopEventRepository topEventRepository;
    private final EventService eventService;

    public TopEventsDataGenerator(TopEventRepository topEventRepository, EventService eventService) {
        this.topEventRepository = topEventRepository;
        this.eventService = eventService;
    }


    @PostConstruct
    private void generateTopEvent() {
        if (topEventRepository.findAll().size() > 0) {
            LOGGER.debug("top events already generated");
        } else {
            LOGGER.debug("generating {} top event entities", NUMBER_OF_EVENTS_TO_GENERATE);
            for (String c: eventService.categoriesToStringList()) {
                for (int i = 0; i < NUMBER_OF_EVENTS_TO_GENERATE; i++) {
                    long soldTickets = new Random().nextLong();
                    soldTickets = soldTickets < 0 ? soldTickets * (-1) : soldTickets;
                    SoldTicketsPerEvent soldTicketsPerEvent = SoldTicketsPerEvent.SoldTicketBuilder.aSoldTicket()
                        .withTitle(TEST_EVENT_TITLE + " " + c + " " +i)
                        .withCategory(c)
                        .withAccumulatedSoldTickets(soldTickets)
                        .build();
                    LOGGER.debug("saving top event {}", soldTicketsPerEvent);
                    topEventRepository.save(soldTicketsPerEvent);
                }
            }
        }
    }

}
