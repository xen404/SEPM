package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import at.ac.tuwien.sepm.groupphase.backend.entity.Merchandise;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MerchandiseRepository extends JpaRepository<Merchandise, Long> {

    @Query("SELECT m FROM Merchandise m WHERE m.bonus = TRUE AND m.bonusPoints <= ?1 ORDER BY m.bonusPoints")
    List<Merchandise> findMerchandiseLessThanOrEqualToBonusPointsAsc(Long bonusPoints);

    @Query("SELECT m FROM Merchandise m WHERE m.event = :event")
    List<Merchandise> findAllMerchandiseItemsForEvent(@Param("event") Event event);

    @Query("SELECT m FROM Merchandise m WHERE m.bonus = TRUE AND m.bonusPoints <= ?1 ORDER BY m.bonusPoints")
    Page<Merchandise> findMerchandiseLessThanOrEqualToBonusPointsAsc(Long bonusPoints, Pageable pageable);
}
