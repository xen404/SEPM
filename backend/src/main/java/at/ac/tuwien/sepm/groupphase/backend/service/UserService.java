package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;

import at.ac.tuwien.sepm.groupphase.backend.entity.EditedUser;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

public interface UserService extends UserDetailsService {

    /**
     * Find a user in the context of Spring Security based on the email address
     *
     * <p>For more information have a look at this tutorial:
     * https://www.baeldung.com/spring-security-authentication-with-a-database
     *
     * @param email the email address
     * @return a Spring Security user
     * @throws UsernameNotFoundException is thrown if the specified user does not exists
     */
    @Override
    UserDetails loadUserByUsername(String email) throws UsernameNotFoundException;

    /**
     * Find an application user based on the email address.
     *
     * @param email the email address
     * @return a application user
     */
    ApplicationUser findUserByEmail(String email);

    /**
     * Find all user entries.
     *
     * @param pageable page configuration
     * @return ordered list of all user entries
     */
    Page<ApplicationUser> findAll(Pageable pageable);

    /**
     * Find all user entries with specified access ordered by email (descending).
     *
     * @param access of user entries.
     * @return ordered list of all user entries.
     */
    List<ApplicationUser> findAll(boolean access);

    /**
     * Save a new user.
     *
     * @param userSignUp the user data.
     * @return a signed up user.
     */
    ApplicationUser saveUser(ApplicationUser userSignUp);

    /**
     * Locks or unlocks access to user account.
     *
     * @param email the users email.
     * @param access of user account.
     * @return an user.
     */
    ApplicationUser setUserAccess(String email, boolean access);

    /**
     * Deletes specified user from the repository.
     *
     * @param id of the user to be deleted
     * @throws IllegalArgumentException if id is smaller than 1
     * @throws NotFoundException if user with the given id does not exist
     */
    void deleteUserById(Long id);

    /**
     * Updates user in the repository.
     *
     * @param userUpdated Application user containing all the details of the user
     * @return updated ApplicationUser that is stored in repository
     */
    ApplicationUser updateUser(EditedUser userUpdated);

    /**
     * Searches user who's email address fits search criteria.
     *
     * @param pageable page configuration
     * @param criteria the search criteria.
     * @return users who's email address fits search criteria.
     */
    Page<ApplicationUser> searchUser(String criteria, Pageable pageable);

    /**
     * Resets oder increases Login Count of User who tried to login.
     * @param email of user to update login count.
     * @param password of user to update login count.
     */
    void updateLoginCount(String email, String password);

    /**
     * Updates the bonus points of the specified user.
     *
     * @param userId specifies the user who's bonus points are being updated
     * @param bonusPoints the amount of bonus points (can be positive or negative)
     * @return updated user that is stored in the repository
     */
    ApplicationUser updateBonusPoints(Long userId, Long bonusPoints);
}
