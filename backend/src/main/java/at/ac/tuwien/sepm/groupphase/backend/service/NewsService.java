package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.News;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface NewsService {


    /**
     * Find a single news entry by id.
     *
     * @param id the id of the news entry
     * @return the news entry
     */
    News findOne(Long id);

    /**
     * Publish a single news entry.
     *
     * @param news to publish
     * @return published news entry
     */
    News publishNews(News news);


    /**
     * Find all unseen news for this user.
     *
     * @param pageable page configuration
     * @param userEmail to get the current logged user
     * @return unseen news for this user
     */
    Page<News> findUnseenNews(String userEmail, Pageable pageable);

    /**
     * Set news as seen fot current user.
     *
     * @param id for current news
     * @param userEmail to get the current logged user
     * @return piece of news that was seen
     */
    News  setSeenNews(Long id, String userEmail);

    /**
     * Save image for this news.
     *
     * @param id to get the current news
     * @param file with current image
     */
    void uploadImage(Long id, MultipartFile file);

    /**
     * Get image for this news.
     *
     * @param id the id of this news
     * @return iamge in byte array
     */
    String getImage(Long id);

    /**
     * Find all seen news for this user.
     *
     * @param userEmail to get the current logged user
     * @param pageable page configuration
     * @return seen news for this user
     */

    Page<News> findSeenNews(String userEmail, Pageable pageable);

    /**
     * Get last added News.
     *
     * @return last news added in the repository
     */
    int getLastNews();

    /**
     * Find first 3 pieces of news ordered descending by date.
     *
     * @param userEmail to get the current logged user
     * @return first 3 unseen news for this user
     */
    List<News> findLastUnseenNews(String userEmail);



}
