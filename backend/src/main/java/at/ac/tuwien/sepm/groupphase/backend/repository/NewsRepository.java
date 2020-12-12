package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.News;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {


    /**
     * Find all news for te current logged in user.
     *
     * @param id id for current user
     * @return list with all the news for this user
     */
    @Query(value = "SELECT news_id from news_seen where user_id=:id ", nativeQuery = true)
    List<Long> findNewsForCurrentUser(@Param("id") Long id);

    /**
     * Find all seen news for this user.
     *
     * @param userId id for the current user.
     * @param page   page configuration
     * @return seen news for this user
     */
    @Query(value = "SELECT * FROM news n WHERE n.id NOT IN (SELECT ns.news_id FROM news_seen ns "
        + "WHERE ns.user_id=:userId) ORDER BY n.published_at DESC", nativeQuery = true)
    Page<News> findSeenNews(@Param("userId") Long userId, Pageable page);


    /**
     * Find all unseen news for this user.
     *
     * @param page   page configuration
     * @param userId id for the current user.
     * @return unseen news for this user
     */
    @Query(value = "SELECT * FROM news n INNER JOIN news_seen ns ON n.id=ns.news_id "
        + "WHERE ns.user_id=:userId ORDER BY n.published_at DESC", nativeQuery = true)
    Page<News> findUnseenNews(@Param("userId") Long userId, Pageable page);

    /**
     * Find first 3 pieces of news ordered descending by date.
     *
     * @param userId id of the current user
     * @return first 3 unseen news for this user
     */
    @Query(value = "SELECT * FROM news n INNER JOIN news_seen ns ON n.id=ns.news_id WHERE "
        + "ns.user_id=:userId ORDER BY n.published_at DESC LIMIT 3", nativeQuery = true)
    List<News> findLastUnseenNews(@Param("userId") Long userId);


    /**
     * Delete from joint table the seen news for this user.
     *
     * @param userId id for current user
     * @param newsId id for a specific piece of news for this user
     */
    @Query(value = "DELETE FROM news_seen WHERE user_id=:userId AND news_id= :newsId", nativeQuery = true)
    @Modifying
    @Transactional
    void deleteSeenNews(@Param("userId") Long userId, @Param("newsId") Long newsId);
}
