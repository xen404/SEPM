package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.Artist;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ArtistService {

    /**
     * Find all artist entries ordered by name.
     * @return ordered list of all artist entries
     */
    List<Artist> findAllByOrderByName();

    /**
     * Find all artist entries ordered by name.
     *
     * @param page for pagination
     * @return ordered list of all artist entries
     */
    Page<Artist> findAllByOrderByName(int page);

    /**
     * Find a single message entry by id.
     *
     * @param id of artist entry
     * @return the artist entry
     */
    Artist findOne(Long id);

    /**
     * Find artists with the given name.
     *
     * @param name of artist to be found
     * @param page for pagination
     * @return a list of artist
     */
    Page<Artist> findByNameContainingIgnoreCase(String name, Integer page);
}
