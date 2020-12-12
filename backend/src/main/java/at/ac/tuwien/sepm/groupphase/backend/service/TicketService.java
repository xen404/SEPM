package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.Show;
import at.ac.tuwien.sepm.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;

public interface TicketService {

    /**
     * Find a single show entry by id
     *
     * @return the show entry
     * @throws NotFoundException if there is no show with this id
     */
    Ticket findOne(Long id);

}
