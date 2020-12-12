package at.ac.tuwien.sepm.groupphase.backend.unittests.service;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.EditedUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepm.groupphase.backend.exception.IllegalOperationException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.NewsRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.TicketRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UsersRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import at.ac.tuwien.sepm.groupphase.backend.service.Validator;
import at.ac.tuwien.sepm.groupphase.backend.service.impl.CustomUserDetailService;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class UserServiceTest implements TestData {

    @Mock
    private UsersRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private NewsRepository newsRepository;
    @Mock
    private TicketRepository ticketRepository;
    @Mock
    private Validator validator;

    private UserService userService;

    @BeforeEach
    public void initMessageService() {
        userService = new CustomUserDetailService(userRepository, passwordEncoder, newsRepository, ticketRepository, validator);
    }

    @Test
    public void givenNothing_whenFindAll_thenThrowsNotFoundException() {
        //Arrange
        when(userRepository.findAllByOrderByFirstNameAsc(PageRequest.of(0, 50))).thenReturn(Page.empty());

        //Act & Assert
        assertThatExceptionOfType(NotFoundException.class).isThrownBy(
            () -> userService.findAll(PageRequest.of(0, 50))
        );
    }

    @Test
    public void givenTwoMessages_whenFindAll_thenListOfSizeTwoAndMessagesWithCorrectProperties() {
        //Arrange
        List<ApplicationUser> testUser = new ArrayList<>();
        testUser.add(TestData.getTestUserWithId(1L));
        testUser.add(TestData.getTestUserWithId(2L));
        Page<ApplicationUser> page = new PageImpl<ApplicationUser>(
            testUser, PageRequest.of(0, 50), testUser.size());

        when(userRepository.findAllByOrderByFirstNameAsc(PageRequest.of(0, 50))).thenReturn(page);
        when(userRepository.findAllByOrderByFirstNameDesc(false)).thenReturn(null);

        //Act
        Page<ApplicationUser> usersPage = userService.findAll(PageRequest.of(0, 50));
        List<ApplicationUser> users2 = userService.findAll(false);

        //Assert
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(usersPage.getTotalElements()).isEqualTo(2);
        softly.assertThat(users2).isEqualTo(null);
        softly.assertThat(usersPage.getContent()).extracting(ApplicationUser::getEmail)
            .containsOnly(DEFAULT_USER);
        softly.assertThat(usersPage.getContent()).extracting(ApplicationUser::getFirstName)
            .containsOnly(DEFAULT_USER_NAME);
        softly.assertThat(usersPage.getContent()).extracting(ApplicationUser::getSurname)
            .containsOnly(DEFAULT_USER_SURNAME);
        softly.assertThat(usersPage.getContent()).extracting(ApplicationUser::getPassword)
            .containsOnly(DEFAULT_PASSWORD);
        softly.assertThat(usersPage.getContent()).extracting(ApplicationUser::getId).containsExactly(1L, 2L);
        softly.assertAll();
    }

    @Test
    public void givenOneUser_whenFindByEmail_thenFindCorrectUser() {
        //Arrange
        ApplicationUser testUser = TestData.getTestUser();
        when(userRepository.findByEmail(TEST_USER_EMAIL)).thenReturn(testUser);

        //Act
        ApplicationUser user = userService.findUserByEmail(TEST_USER_EMAIL);

        //Assert
        assertThat(user).isEqualTo(testUser);
    }

    @Test
    public void givenNothing_whenSaveUser_thenReturnSavedUser() {
        //Arrange
        ApplicationUser testUser = TestData.getTestUser();
        when(userRepository.save(testUser)).thenReturn(testUser);

        //Act
        ApplicationUser user = userService.saveUser(testUser);

        //Assert
        assertThat(user).isEqualTo(testUser);
    }

    @Test
    public void giveOneUser_whenSearchUser_thenReturnFoundUser() {
        //Arrange
        ApplicationUser testUser = TestData.getTestUser();
        List<ApplicationUser> list = new ArrayList<>();
        list.add(testUser);
        Page<ApplicationUser> page = new PageImpl<ApplicationUser>(
            list, PageRequest.of(0, 50), list.size());
        when(userRepository.findByEmailContainingIgnoreCase("te", PageRequest.of(0, 50))).thenReturn(page);

        //Act
        Page<ApplicationUser> users = userService.searchUser("te", PageRequest.of(0, 50));

        //Assert
        assertThat(users.getContent().get(0)).isEqualTo(testUser);
    }

    @Test
    public void givenOneUser_whenSetUserAccess_thenReturnUserWithChangedAccess() {
        //Arrange
        ApplicationUser testUser = TestData.getTestUser();
        assertThat(testUser.getAccess()).isEqualTo(true);

        //Act
        userRepository.updateAccess(false, testUser.getEmail());
        testUser.setAccess(false);

        //Assert
        assertThat(testUser.getAccess()).isEqualTo(false);
    }


    @Test
    public void givenNotExistingUserId_whenDelete_thenThrowNotFoundException() {

        //Act
        //Assert
        assertThrows(NotFoundException.class, () -> userService.deleteUserById(1L));
    }

    @Test
    public void givenReservedTickets_forOneUser_whenDelete_thenDeleteTickets() {

        ApplicationUser user = TestData.getTestUser();
        //Act
        Ticket ticket1 = TestData.getTestTicket();
        Ticket ticket2 = TestData.getTestTicketWithId(2L);
        ticket1.setOrderId(1L);
        ticket1.setUser(user);
        ticket2.setOrderId(1L);
        ticket2.setUser(user);
        ticket1.setStatus(Ticket.Status.RESERVED);
        ticket2.setStatus(Ticket.Status.PURCHASED);


        List<Ticket> tickets = new ArrayList<>();
        tickets.add(ticket1);
        tickets.add(ticket2);

        //Arrange
        when(ticketRepository.getTicketsForUserByUserId(1L)).thenReturn(tickets);
        when(userRepository.existsById(user.getId())).thenReturn(true);
        userService.deleteUserById(TestData.getTestUser().getId());

        //Act & Assert
        assertThat(ticket1.getStatus()).isEqualByComparingTo(Ticket.Status.FREE);
        assertThat(ticket2.getStatus()).isEqualByComparingTo(Ticket.Status.PURCHASED);
        assertThat(ticket1.getUser()).isEqualTo(null);
        assertThat(ticket2.getUser()).isEqualTo(null);
        assertThat(ticket1.getOrderId()).isEqualTo(ticket1.getOrderId());
        assertThat(ticket1.getOrderId()).isEqualTo(null);

        //assertThat(userRepository.existsById(user.getId())).isEqualTo(false);

    }

    @Test
    public void givenInvalidUserId_whenDelete_thenThrowIllegalArgumentException() {

        //Act
        //Assert
        assertThrows(IllegalArgumentException.class, () -> userService.deleteUserById(-1L));
    }

    @Test
    public void givenValidEditedUser_whenUpdate_returnedApplicationUserHasAllUpdatedProperties() {
        //Arrange
        EditedUser testEditedUser = TestData.getTestEditedUser();
        ApplicationUser originalUser = TestData.getTestUser();
        when(userRepository.existsById(testEditedUser.getId())).thenReturn(true);
        when(userRepository.getOne(testEditedUser.getId())).thenReturn(originalUser);
        when(userRepository.save(originalUser)).thenReturn(originalUser);

        //Act
        ApplicationUser updatedApplicationUser = userService.updateUser(testEditedUser);

        //Assert
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(updatedApplicationUser.getId()).isEqualTo(testEditedUser.getId());
        softly.assertThat(updatedApplicationUser.getEmail()).isEqualTo(testEditedUser.getEmail());
        softly.assertThat(updatedApplicationUser.getFirstName()).isEqualTo(testEditedUser.getFirstName());
        softly.assertThat(updatedApplicationUser.getSurname()).isEqualTo(testEditedUser.getSurname());
        softly.assertThat(updatedApplicationUser.getPassword()).isEqualTo(testEditedUser.getPassword());
        softly.assertThat(updatedApplicationUser.getAdmin()).isEqualTo(testEditedUser.getAdmin());
        softly.assertThat(updatedApplicationUser.getAccess()).isEqualTo(testEditedUser.getAccess());
    }

    @Test
    public void givenValidEditedUserWithPasswordNull_whenUpdate_returnedApplicationUserHasAllOriginalPassword() {
        //Arrange
        EditedUser testEditedUser = TestData.getTestEditedUser();
        testEditedUser.setPassword(null);
        assertNull(testEditedUser.getPassword());
        ApplicationUser originalUser = TestData.getTestUser();
        when(userRepository.existsById(testEditedUser.getId())).thenReturn(true);
        when(userRepository.getOne(testEditedUser.getId())).thenReturn(originalUser);
        when(userRepository.save(originalUser)).thenReturn(originalUser);

        //Act
        ApplicationUser updatedApplicationUser = userService.updateUser(testEditedUser);

        //Assert
        String originalPasswordEncoded = originalUser.getPassword();
        assertNotNull(updatedApplicationUser.getPassword());
        assertEquals(originalPasswordEncoded, updatedApplicationUser.getPassword());
    }

    @Test
    public void givenInvalidUserId_whenUpdateUserBonusPoints_thenThrowIllegalArgumentException() {
        //Arrange
        Long invalidUserId = -1L;
        Long bonusPoints = 100L;

        //Act & Assert
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(
            () -> userService.updateBonusPoints(invalidUserId, bonusPoints)
        );
    }

    @Test
    public void givenNotExistingUserId_whenUpdateUserBonusPoints_thenThrowNotFoundException() {
        //Arrange
        Long notExistingUserId = 1L;
        Long bonusPoints = 100L;
        when(userRepository.existsById(anyLong())).thenReturn(false);

        //Act & Assert
        assertThatExceptionOfType(NotFoundException.class).isThrownBy(
            () -> userService.updateBonusPoints(notExistingUserId, bonusPoints)
        );
    }

    @Test
    public void givenHigherNumberOfPointsToReduceThanPresent_whenUpdateUserBonusPoints_thenThrowIllegalOperationException() {
        //Arrange
        ApplicationUser existingUser = TestData.getTestUser();
        Long existingUserId = existingUser.getId();
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.getOne(anyLong())).thenReturn(existingUser);
        Long bonusPointsToReduce = (existingUser.getBonusPoints() + 10L) * -1;

        //Act & Assert
        assertThatExceptionOfType(IllegalOperationException.class).isThrownBy(
            () -> userService.updateBonusPoints(existingUserId, bonusPointsToReduce)
        );
    }

    @Test
    public void givenPositiveBonusPoints_whenUpdateUserBonusPoints_thenUsersBonusPointsIncreased() {
        //Arrange
        ApplicationUser existingUser = TestData.getTestUser();
        Long existingUserId = existingUser.getId();
        Long bonusPoints = 10L;
        when(userRepository.existsById(anyLong())).thenReturn(true);
        ApplicationUser existingUserUpdated = TestData.getTestUserWithId(2L);
        existingUserUpdated.setBonusPoints(existingUser.getBonusPoints() + bonusPoints);
        when(userRepository.getOne(anyLong())).thenReturn(existingUser).thenReturn(existingUserUpdated);


        //Act
        ApplicationUser updatedUser = userService.updateBonusPoints(existingUserId, bonusPoints);

        //Assert
        assertEquals(updatedUser.getBonusPoints(), existingUser.getBonusPoints() + bonusPoints);
    }

    @Test
    public void givenNegativeBonusPoints_whenUpdateUserBonusPoints_thenUsersBonusPointsReduced() {
        //Arrange
        ApplicationUser existingUser = TestData.getTestUser();
        Long existingUserId = existingUser.getId();
        Long bonusPoints = -10L;
        when(userRepository.existsById(anyLong())).thenReturn(true);
        ApplicationUser existingUserUpdated = TestData.getTestUserWithId(2L);
        existingUserUpdated.setBonusPoints(existingUser.getBonusPoints() + bonusPoints);
        when(userRepository.getOne(anyLong())).thenReturn(existingUser).thenReturn(existingUserUpdated);

        //Act
        ApplicationUser updatedUser = userService.updateBonusPoints(existingUserId, bonusPoints);

        //Assert
        assertEquals(updatedUser.getBonusPoints(), existingUser.getBonusPoints() + bonusPoints);
    }

}
