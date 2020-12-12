package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Show;
import at.ac.tuwien.sepm.groupphase.backend.entity.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    /**
     * Find all ticket entries of the specified show.
     * @param show show whose tickets to find
     * @return list of all tickets of the specified show
     */
    List<Ticket> findByShow(Show show);

    /**
     * Creates a sequence used to number orders.
     */
    @Transactional
    @Modifying
    @Query(value = "DROP SEQUENCE IF EXISTS order_id_seq; CREATE SEQUENCE order_id_seq START 1", nativeQuery = true)
    void createSequence();

    /**
     * Get the next order id value.
     * @return next order id
     */
    @Query(value = "SELECT nextval('order_id_seq')", nativeQuery = true)
    Long getNextOrderId();

    @Query(value = "SELECT * FROM ticket WHERE user_id=:userId AND order_id=:orderId", nativeQuery = true)
    List<Ticket> getTicketsForUserByOrderId(@Param("userId") Long userId, @Param("orderId") Long orderId);

    /**
     * Get all tickets entries of the specified user.
     * @param id user id
     * @return list of all tickets of the specified user
     */
    @Query(value = "SELECT * FROM ticket WHERE user_id =:userId", nativeQuery = true)
    List<Ticket> getTicketsForUserByUserId(@Param("userId") Long id);

    /**
     * Get all tickets entries of the specified user.
     * @param id user id
     * @return page of all tickets of the specified user
     */
    @Query(value = "SELECT * FROM ticket WHERE user_id =:userId", nativeQuery = true)
    Page<Ticket> getPageWithAllTicketsByUserId(@Param("userId") Long id, Pageable pageable);

    /**
     * Find all ticket entries of the specified order.
     * @param orderId order id whose tickets to find
     * @return list of all tickets of the specified order
     */
    List<Ticket> findByOrderId(Long orderId);
}
