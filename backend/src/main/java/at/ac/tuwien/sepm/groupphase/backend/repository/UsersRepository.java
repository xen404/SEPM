package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface UsersRepository extends JpaRepository<ApplicationUser, Long>,
    PagingAndSortingRepository<ApplicationUser, Long> {

    /**
     * Find all user entry by email.
     *
     * @param email of user entry.
     * @return user entry.
     */
    ApplicationUser findByEmail(@Param("email")String email);

    /**
     * Change access of user entry with given email.
     *
     * @param access to change user entry access to.
     * @param email of user entry to modify.
     */
    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE users SET access = :access WHERE email = :email", nativeQuery = true)
    void updateAccess(@Param("access") boolean access, @Param("email") String email);

    /**
     * Find all user entries ordered by email (descending).
     *
     * @return ordered list of all user entries.
     */
    List<ApplicationUser> findAllByOrderByFirstNameDesc();

    /**
     * Find all user entries with specified access ordered by email (descending).
     *
     * @param access of user entries.
     * @return ordered list of all user entries.
     */
    @Query(value = "SELECT * FROM users u where u.access = :access", nativeQuery = true)
    List<ApplicationUser> findAllByOrderByFirstNameDesc(@Param("access") boolean access);

    /**
     * Find all user entries ordered by email (descending).
     * @param pageable page configuration
     * @return ordered list of all user entries.
     */
    Page<ApplicationUser> findAllByOrderByFirstNameAsc(Pageable pageable);

    /**
     * Searches users who's email address fits search criteria.
     *
     * @param criteria find all users with email
     * @param pageable page configuration
     * @return List of artists with this criteria in email
     */
    Page<ApplicationUser> findByEmailContainingIgnoreCase(String criteria, Pageable pageable);

    /**
     * Updates the users bonus points.
     *
     * @param id specifies the user to be updated
     * @param bonusPoints the new bonus points for the user
     *                    must be >= 0
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE ApplicationUser u SET u.bonusPoints = :bonusPoints WHERE u.id = :id")
    void updateBonusPoints(@Param("id") Long id, @Param("bonusPoints") Long bonusPoints);

    /**
     * Finds all Customers.
     *
     * @return List of Customers
     */
    List<ApplicationUser> findAllByAdminFalse();
}

