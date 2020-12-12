package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import at.ac.tuwien.sepm.groupphase.backend.entity.Location;
import at.ac.tuwien.sepm.groupphase.backend.entity.Show;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import util.EventCategory;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {


    /**
     * find all events by category.
     *
     * @param id of category
     * @return list of ids
     */
    @Query(value = "SELECT id from event where category=:id", nativeQuery = true)
    List<Long> findAllByCategory(@Param("id") Long id);

    /**
     * find news for user.
     *
     * @param id of user
     * @return list of ids
     */
    @Query(value = "SELECT news_id from news_seen where user_id=:id ", nativeQuery = true)
    List<Long> findNewsForCurrentUser(@Param("id")Long id);

    /**
     * find all events.
     *
     * @return a list of events
     */
    List<Event> findAll();

    /**
     * Find all event entries with pagination.
     *
     * @param pageable for pagination
     * @return list of al show entries
     */
    Page<Event> findAllByOrderByTitle(Pageable pageable);

    /**
     * find all events with given params.
     *
     * @param title of event
     * @param categoryId of event
     * @param duration of event
     * @param pageable of event
     * @return events
     */
    @Query(value = "SELECT * FROM event e WHERE ( e.title ILIKE CONCAT('%',:title,'%')) AND (:categoryId = -1  OR e.category = :categoryId) AND (:duration = -1  OR e.duration >= :duration-30 AND e.duration <= :duration+30 )", nativeQuery = true)
    Page<Event> findByParam(@Param("title") String title, @Param("categoryId") Long categoryId, @Param("duration") Integer duration, Pageable pageable);

    /**
     * Find all shows of location with id.
     * @param id of location
     * @return list of event ids of artist with id
     */
    @Query(value = "SELECT event_id from Event_Artist_Jtable where artist_id=:id ", nativeQuery = true)
    List<Long> findEvent_IDByArtist_ID(@Param("id")Long id);

    Page<Event> findAllByIdIn(List<Long> id, Pageable pageable);

}
