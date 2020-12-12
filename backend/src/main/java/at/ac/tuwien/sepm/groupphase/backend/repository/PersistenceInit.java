package at.ac.tuwien.sepm.groupphase.backend.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;

@Component
@ConditionalOnProperty(value = "spring.jpa.hibernate.ddl-auto", havingValue = "create")
public class PersistenceInit {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    private TicketRepository ticketRepository;

    @EventListener
    public void onApplicationLoaded(ContextRefreshedEvent event) {
        LOGGER.info("Create order id sequence for tickets in persistence");
        ticketRepository.createSequence();
    }

}
