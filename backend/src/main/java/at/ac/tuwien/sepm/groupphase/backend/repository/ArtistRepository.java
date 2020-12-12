package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Artist;
import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.OrderBy;
import java.util.List;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, Long> {

    /**
     * Find all artist entries.
     *
     * @return list of al artist entries
     */
    List<Artist> findAllByOrderByName();

    /**
     * Find all artist entries.
     *
     * @param pageable for pagination
     * @return list of al artist entries
     */
    Page<Artist> findAllByOrderByName(Pageable pageable);

    /**
     * Find all artists with given name.
     *
     * @param name find all artists with name
     * @param pageable for pagination
     * @return List of artists with this name
     */
    Page<Artist> findByNameContainingIgnoreCase(String name, Pageable pageable);

    /**
     * Find all artists with given name.
     *
     * @param name find all artists with name
     * @return List of artists with this name
     */
    List<Artist> findByNameContainingIgnoreCase(String name);
}
