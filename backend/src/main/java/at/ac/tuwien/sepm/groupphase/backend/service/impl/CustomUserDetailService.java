package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.EditedUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.News;
import at.ac.tuwien.sepm.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepm.groupphase.backend.exception.IllegalOperationException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.NewsRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.TicketRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UsersRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.invoke.MethodHandles;
import java.util.List;
import at.ac.tuwien.sepm.groupphase.backend.service.Validator;

@Service
public class CustomUserDetailService implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final NewsRepository newsRepository;
    private final TicketRepository ticketRepository;
    private final Validator validator;


    @Autowired
    public CustomUserDetailService(UsersRepository usersRepository, PasswordEncoder passwordEncoder,
                                   NewsRepository newsRepository, TicketRepository ticketRepository,
                                   Validator validator) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
        this.newsRepository = newsRepository;
        this.ticketRepository = ticketRepository;
        this.validator = validator;
    }

    @Override
    public UserDetails loadUserByUsername(final String email) throws UsernameNotFoundException {
        LOGGER.debug("Load all user by email");
        try {
            ApplicationUser applicationUser = findUserByEmail(email);
            if (applicationUser.getLoginCount() >= 5) {
                applicationUser.setAccess(false);
                usersRepository.save(applicationUser);
            }
            if (!applicationUser.getAccess()) {
                LOGGER.warn("User with email {} is blocked", email);
                throw new ValidationException("Access to User Account was locked");
            }

            List<GrantedAuthority> grantedAuthorities;
            if (applicationUser.getAdmin()) {
                grantedAuthorities = AuthorityUtils.createAuthorityList("ROLE_ADMIN", "ROLE_USER");
            } else {
                grantedAuthorities = AuthorityUtils.createAuthorityList("ROLE_USER");
            }

            return new User(applicationUser.getEmail(), applicationUser.getPassword(), grantedAuthorities);
        } catch (NotFoundException e) {
            LOGGER.warn("User account with email {} does not exist", email);
            throw new UsernameNotFoundException("User account with email " + email + " does not exist.");
        }
    }

    public void updateLoginCount(String email, String password) {
        ApplicationUser storedUser = findUserByEmail(email);
        if (passwordEncoder.matches(password, storedUser.getPassword())) {
            LOGGER.debug("Increase loginCount of user with id {}", storedUser.getId());
            storedUser.resetLoginCount();
        } else {
            LOGGER.debug("Reset loginCount of user with id {}", storedUser.getId());
            storedUser.updateLoginCount();
        }
        usersRepository.save(storedUser);
    }

    @Override
    public ApplicationUser findUserByEmail(String email) {
        LOGGER.debug("Find application user by email");
        ApplicationUser userSignUp = usersRepository.findByEmail(email);
        if (userSignUp != null) {
            return userSignUp;
        } else {
            LOGGER.warn("Could not find the user with the email address {}", email);
            throw new NotFoundException(String.format("Could not find the user with the email address %s", email));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ApplicationUser> findAll(Pageable pageable) {
        LOGGER.debug("Find all users");
        Page<ApplicationUser> users = usersRepository.findAllByOrderByFirstNameAsc(pageable);
        if (!users.isEmpty()) {
            return users;
        } else {
            throw new NotFoundException("There are no users registered in the system.");
        }
    }

    @Override
    public List<ApplicationUser> findAll(boolean access) {
        LOGGER.debug("Find all users with certain access");
        return usersRepository.findAllByOrderByFirstNameDesc(access);
    }

    @Override
    public ApplicationUser saveUser(ApplicationUser applicationUser) {
        LOGGER.debug("Save new user");
        validator.validateUser(applicationUser);
        List<News> seenNews = newsRepository.findAll();

        for (News news : seenNews) {
            applicationUser.getNews().add(news);
        }
        //check if email isn't already in use
        ApplicationUser testUser = usersRepository.findByEmail(applicationUser.getEmail());
        if (testUser == null) {
            applicationUser.setPassword(passwordEncoder.encode(applicationUser.getPassword()));
            return usersRepository.save(applicationUser);
        } else {
            LOGGER.warn("E-mail address {} already used with another account.", testUser.getEmail());
            throw new ValidationException("E-mail address already used with another account.");
        }
    }

    @Override
    @Transactional
    public ApplicationUser setUserAccess(String email, boolean access) {
        LOGGER.debug("Lock user");
        usersRepository.updateAccess(access, email);
        return usersRepository.findByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ApplicationUser> searchUser(String criteria, Pageable pageable) {
        LOGGER.debug("Search user with: ", criteria);
        Page<ApplicationUser> users = usersRepository.findByEmailContainingIgnoreCase(criteria, pageable);
        if (!users.isEmpty()) {
            return users;
        } else {
            LOGGER.warn("Could not find a user which email address contains {}", criteria);
            throw new NotFoundException("Could not find a user which email address contains " + criteria);
        }
    }

    @Override
    @Transactional
    public void deleteUserById(Long id) {
        LOGGER.debug("Delete user with id {}", id);
        if (id <= 0) {
            LOGGER.warn("Id must be greater than zero! Currently id = {}", id);
            throw new IllegalArgumentException("Id must be greater than zero");
        }
        if (usersRepository.existsById(id)) {

            List<Ticket> tickets = ticketRepository.getTicketsForUserByUserId(id);
            if (!tickets.isEmpty()) {
                for (Ticket ticket : tickets) {
                    if (ticket.getStatus().compareTo(Ticket.Status.RESERVED) == 0) {
                        ticket.setStatus(Ticket.Status.FREE);
                        ticket.setDateOfReservation(null);
                        ticket.setOrderId(null);
                        ticket.setUser(null);
                    } else {
                        if (ticket.getStatus().compareTo(Ticket.Status.PURCHASED) == 0) {
                            ticket.setUser(null);

                        }
                    }
                    ticketRepository.save(ticket);
                }
            }

            usersRepository.deleteById(id);

        } else {
            LOGGER.warn("Could not find the user with the id {}", id);
            throw new NotFoundException(String.format("Could not find the user with the id %s", id));
        }
    }

    @Override
    @Transactional
    public ApplicationUser updateUser(EditedUser userUpdated) {
        LOGGER.debug("Update user with id {}", userUpdated.getId());
        //check if email isn't already in use
        ApplicationUser originalUser = usersRepository.getOne(userUpdated.getId());
        if (!originalUser.getEmail().equals(userUpdated.getEmail())) {
            if (usersRepository.findByEmail(userUpdated.getEmail()) != null) {
                throw new ValidationException("E-mail address already used with another account.");
            }
        }

        if (usersRepository.existsById(userUpdated.getId())) {
            ApplicationUser userToBeUpdated = usersRepository.getOne(userUpdated.getId());
            userToBeUpdated.setFirstName(userUpdated.getFirstName());
            userToBeUpdated.setSurname(userUpdated.getSurname());
            userToBeUpdated.setEmail(userUpdated.getEmail());
            if (userUpdated.getPassword() != null) {
                userToBeUpdated.setPassword(passwordEncoder.encode(userUpdated.getPassword()));
            }
            usersRepository.save(userToBeUpdated);
            return userToBeUpdated;
        } else {
            LOGGER.warn("Could not find the user with the id {}", userUpdated.getId());
            throw new NotFoundException(String.format(
                "Could not find the user with the id %s", userUpdated.getId()));
        }

    }

    @Override
    @Transactional
    public ApplicationUser updateBonusPoints(Long userId, Long bonusPoints) {
        LOGGER.debug("Update bonus points for user with id {}", userId);
        if (userId <= 0) {
            LOGGER.warn("Id must be greater than zero. Currently id = {}", userId);
            throw new IllegalArgumentException("Id must be greater than zero");
        }
        if (usersRepository.existsById(userId)) {
            if (bonusPoints == 0) {
                return usersRepository.getOne(userId);
            } else if (bonusPoints < 0) {
                LOGGER.debug("Reducing the bonus points of user with id {}", userId);
            } else {
                LOGGER.debug("Increasing the bonus points of user with id {}", userId);
            }
            long currentBonusPoints = usersRepository.getOne(userId).getBonusPoints();
            long newBonusPoints = currentBonusPoints + bonusPoints;
            if (newBonusPoints < 0) {
                LOGGER.warn("Cannot reduce bonus points. User has too few bonus points. Bonus points = {}", newBonusPoints);
                throw new IllegalOperationException("Cannot reduce bonus points. User has too few bonus points");
            }
            usersRepository.updateBonusPoints(userId, newBonusPoints);
            return usersRepository.getOne(userId);
        } else {
            LOGGER.warn("Could not find the user with the id {}", userId);
            throw new NotFoundException(String.format("Could not find the user with the id %s", userId));
        }
    }
}
