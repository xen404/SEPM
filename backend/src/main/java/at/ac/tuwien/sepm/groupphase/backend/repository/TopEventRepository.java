package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.SoldTicketsPerEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;

@Repository
public interface TopEventRepository extends JpaRepository<SoldTicketsPerEvent, Long> {

    @Query(value = "select a.id, a.title, a.category, count (*) as accumulated_sold_tickets " +
        "from Event a, Show b, Ticket c " +
        "where b.event_id  = a.id " +
        "and c.show_id  = b.id " +
        "and a.category = :category " +
        "and c.date_of_purchase between :startDate and :endDate " +
        "group by a.id, a.title, a.category " +
        "order by count (*) desc " +
        "limit 10;", nativeQuery=true)
    List<SoldTicketsPerEvent> findTopTenByCategory(@Param("category") int category, @Param("startDate") Date startDate, @Param("endDate") Date endDate);
}
