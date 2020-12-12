package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.Merchandise;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MerchandiseService {

    /**
     * Save a merchandise item to the repository.
     *
     * @param merchandise the merchandise item to be saved, containing all the necessary information
     * @return the merchandise item that has been saved to the repository
     */
    Merchandise save(Merchandise merchandise);

    /**
     * Get all merchandise items.
     *
     * @param pageable page configuration
     * @return List of all the stored merchandise items
     */
    Page<Merchandise> getAllMerchandiseItems(Pageable pageable);

    /**
     * Get all the merchandise items for a specified event.
     *
     * @param eventId specifies the event
     * @return List of merchandise items that belong to the specified event
     */
    List<Merchandise> getMerchandiseForEvent(Long eventId);

    /**
     * Get all merchandise items that are available for certain bonus points.
     *
     * @param bonusPoints the upper limit for the bonus points
     * @param pageable page configuration
     * @return List of merchandise items which bonus points are <= the specified bonus points
     */
    Page<Merchandise> getAvailableMerchandise(Long bonusPoints, Pageable pageable);

    /**
     * Get specified merchandise item.
     *
     * @param id specifies the merchandise item
     * @return the merchandise item corresponding to the specified id
     */
    Merchandise getMerchandiseById(Long id);

    /**
     * Get index for the next merchandise item to be added.
     * @return index for the next merchandise item
     */
    Long getNextIndex();

    /**
     * Save an image to a specified merchandise item to the repository.
     * @param id specifies the merchandise item to which the image is stored
     * @param file the image to be stored
     */
    void uploadImage(Long id, MultipartFile file);

    /**
     * Get the image for a specified merchandise item.
     * @param id specifies the merchandise item
     * @return base64 encoded image
     */
    String getImage(Long id);
}
