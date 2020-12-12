package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Show;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShowRepository extends JpaRepository<Show, Long> {

    /**
     * Finds the show with the specified id.
     *
     * @param id id of searched show
     * @return show with specified id
     */
    Optional<Show> findById(Long id);

    /**
     * Finds all shows.
     *
     * @return list of all shows
     */
    List<Show> findAll();

    /**
     * Finds all shows order by id (descending).
     *
     * @param pageable for pagination
     * @return page of shows
     */
    Page<Show> findAllByOrderByIdDesc(Pageable pageable);

    /**
     * Find all show entries with pagination.
     *
     * @param pageable for pagination
     * @return list of al show entries
     */
    Page<Show> findAllByOrderByTitle(Pageable pageable);

    /**
     * Find all shows with given title.
     *
     * @param title    find all shows with title
     * @param pageable for pagination
     * @return List of locations
     */
    Page<Show> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    @Query(value = "SELECT DISTINCT s.* FROM show s INNER JOIN event e ON s.event_id = e.id INNER JOIN location l ON s.location_id = l.id INNER JOIN ticket t ON t.show_id = s.id and  e.title ILIKE CONCAT('%',:title,'%') and l.description ILIKE CONCAT('%',:location,'%') and (exists (select t.price group by t.price having t.price <= :price) OR :price = -1) and (s.start_date = Cast(:date as DATE) OR :date like '-1') and (s.start_time >= Cast(:time as TIME) OR :time like '-1')", nativeQuery = true)
    Page<Show> findByParams(@Param("title") String title, @Param("location") String location, @Param("price") int price, @Param("date") String date, @Param("time") String time, Pageable pageable);

    /**
     * Find all shows of location with id.
     *
     * @param id       of location
     * @param pageable for pagination
     * @return page of shows of location with id
     */
    Page<Show> findAllByLocation_Id(Long id, Pageable pageable);
}
