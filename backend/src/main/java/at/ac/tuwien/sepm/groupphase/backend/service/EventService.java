package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import at.ac.tuwien.sepm.groupphase.backend.entity.SoldTicketsPerEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public interface EventService {

    /**
     * Saves events.
     * @param event Event entity to save
     * @return Event entity
     */
    Event saveEvent(Event event);

    /**
     * Get specific event.
     * @param id id of the event
     * @return EVent entity
     */
    Event findOne(Long id);

    /**
     * Get all saved events.
     * @return List containing event entities
     */
    List<Event> findAll();

    /**
     * Get all categories.
     * @return List of category's names
     */
    List<String> categoriesToStringList();

    /**
     * Get all events of specific category.
     * @param id id of the category
     * @return List containing event entities
     */
    List<Event> findAllByCategory(Long id);

    /**
     * Get the ten events where the most tickets have been bought in a specific month for each category.
     *
     * @param month specifies the month of interest
     *              ticket sales between first and last day of this month
     * @return map containing a list of top events for each category
     */
    HashMap<String, List<SoldTicketsPerEvent>> getTopEvents(int month);

    /**
     * Get image for this event.
     *
     * @param id the id of this event
     * @return image in byte array
     */
    String getImage(Long id) throws IOException;

    /**
     * Get last added event.
     *
     * @return last event added in the repository
     */
    int getLastEvent();

    /**
     * Save image for this event.
     *
     * @param id to get the current event
     * @param file with current image
     */
    void uploadImage(Long id, MultipartFile file);

    /**
     * Find all event entries ordered by name.
     *
     * @param page for pagination
     * @return ordered list of all show entries
     */
    Page<Event> findAllByOrderByTitle(int page);

    /**
     * Find events with given params.
     * @param title of event
     * @param category of event
     * @param duration of event (+/- 30 minutes)
     * @param page for pagination
     * @return events
     */
    Page<Event> findByParam(String title, Long category, int duration, int page);


    /**
     * find all events for the artist with id.
     *
     * @param id of artist
     * @param pageable for pagination
     * @return a list of events from artist with id
     */
    Page<Event> findEventsByArtist(Long id, Pageable pageable);
}
