package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.News;
import at.ac.tuwien.sepm.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.ShowRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.TicketRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.TicketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.Optional;

import static org.hibernate.bytecode.BytecodeLogger.LOGGER;

@Service
public class SimpleTicketService implements TicketService {

    private final TicketRepository ticketRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    public SimpleTicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;

    }

    @Override
    public Ticket findOne(Long id) {
        LOGGER.debug("Find ticket with id = {}", id);
        Optional<Ticket> ticket = ticketRepository.findById(id);
        if (ticket.isPresent()) {
            return ticket.get();
        } else {
            LOGGER.warn("Could not find news with id {}", id);
            throw new NotFoundException(String.format("Could not find news with id %s", id));
        }
    }
}
