package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.SaveShow;
import at.ac.tuwien.sepm.groupphase.backend.entity.Seat;
import at.ac.tuwien.sepm.groupphase.backend.entity.Show;
import at.ac.tuwien.sepm.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface ShowService {
    /**
     * Find all show entries.
     *
     * @return list of all show entries
     */
    List<Show> findAll();

    /**
     * Find all show entries as page.
     *
     * @param pageable for  pagination
     * @return Page of show entries
     */
    Page<Show> findAllAsPage(Pageable pageable);

    /**
     * Get all tickets of a show by id.
     *
     * @param id of show
     * @return list of all tickets of the specified show
     * @throws NotFoundException if there is no show with this id
     */
    List<Ticket> getTicketsOfShow(Long id);

    /**
     * Get all seats of a show by id.
     *
     * @param id of show
     * @return list of all seats present at the location where the specified show is held
     * @throws NotFoundException if there is no show with this id
     */
    List<Seat> getSeatsOfShow(Long id);

    /**
     * Get all seats with their corresponding ticket (or null if seat is "NOSEAT").
     *
     * @param id of ticket
     * @return map of seats and tickets, every seat is mapped to its ticket (or to null if seat is "NOSEAT")
     * @throws NotFoundException if there is no show with this id
     */
    Map<Seat, Ticket> getSeatsWithTicket(Long id);

    /**
     * Find a single show entry by id.
     *
     * @param id od show
     * @return the show entry
     * @throws NotFoundException if there is no show with this id
     */
    Show findOne(Long id);


    /**
     * Save a new show.
     *
     * @param saveShow show to save
     * @return saved show
     */
    Show saveShow(SaveShow saveShow);

    /**
     * Order tickets and updates the users bonus points according the sum of the purchased tickets.
     *
     * @param seatIds   of seats to order
     * @param showId    of show to order tickets from
     * @param userEmail of user who wants to order tickets
     * @param mode      either "RESERVE" or "PURCHASE"
     * @return orderId and the bonus point credit for this purchase.
     */
    Long[] orderTickets(List<Long> seatIds, Long showId, String userEmail, String mode);

    /**
     * Purchase previously reserved tickets.
     * Reserved tickets of the same order but not selected for purchase will be set to FREE.
     *
     * @param ticketIds of tickets to purchase
     * @param userEmail of user who wants to purchase tickets
     * @return order id
     */
    Long purchaseReservedTickets(List<Long> ticketIds, String userEmail);

    /**
     * Get tickets for a user by order id.
     *
     * @param userEmail of user
     * @param orderId   of tickets
     * @return tickets in that order
     * @throws NotFoundException if there is no order with this id
     */
    List<Ticket> getTicketsForUserByOrderId(Long orderId, String userEmail);

    /**
     * Cancel a ticket with given id.
     *
     * @param ticketsId list of ticket ids to be deleted
     * @param userEmail current user
     * @return the number of bonus points the user is charged, because hhe/she does not have enough.
     * @throws NotFoundException if ticket is not found
     */
    long cancelTickets(List<Long> ticketsId, String userEmail) throws IllegalAccessError;


    /**
     * Load all tickets of the user.
     *
     * @param id id of the user
     * @return List of Ticket entities
     */
    List<Ticket> getAllTicketsByUserId(Long id);

    /**
     * Find all show entries ordered by name.
     *
     * @param page for pagination
     * @return ordered list of all show entries
     */
    Page<Show> findAllByOrderByTitle(int page);

    /**
     * Find shows with the given name.
     *
     * @param title    of show to be found
     * @param location of show to be found
     * @param page     for pagination
     * @param price    for searched show
     * @param date     of searched dhow
     * @param time     of searched show
     * @return a list of artist
     */
    Page<Show> findByParams(String title, String location, int price, String date, String time, Integer page);


    /**
     * find all shows for the location with id.
     *
     * @param id       of location
     * @param pageable for pagination
     * @return a List of Shows from location with id
     */
    Page<Show> findShowsByLocation(Long id, Pageable pageable);

    Page<Ticket> getPageWithAllTicketsByUserId(Long id, int page);

    /**
     * generated PDF bill for canceled tickets.
     *
     * @param orderId     id of current order
     * @param ids         of tickets that are deleted to be displayed on the bill
     * @param email       from current user
     * @param bonusPoints add bonusPoints to tickets
     * @return PDF bill with canceled tickets
     */

    ResponseEntity<InputStreamResource> ticketCancelReport(Long orderId,
                                                           List<Long> ids,
                                                           String email,
                                                           Long bonusPoints);

    /**
     * generate PDF for purchased tickets.
     *
     * @param orderId id of current order
     * @param email   from current user
     * @return PDF bill with canceled tickets
     */
    ResponseEntity<InputStreamResource> ticketPurchaseReport(Long orderId, String email) throws Exception;

}
