package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ApplicationUserDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.EditedUserDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.UserMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.repository.TicketRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UsersRepository;
import at.ac.tuwien.sepm.groupphase.backend.security.JwtTokenizer;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class UserEndpointTest implements TestData {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsersRepository userRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    private PasswordEncoder encoder;

    ApplicationUser user;
    ApplicationUserDto userDto;

    @BeforeEach
    public void beforeEach() {
        ticketRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
        user = TestData.getTestUserWithId(1L, encoder);
        userDto = TestData.getTestUserDtoWithId(1L);
    }

    //**************
    //***  GET   ***
    //**************
    @Test
    public void givenOneUser_whenFindAll_thenReturnUser()
        throws Exception {
        userRepository.save(user);

        MvcResult mvcResult = this.mockMvc.perform(get(USER_BASE_URI + "/pages/0")
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        assert(response.getContentAsString().contains(user.getEmail()));
        assert(response.getContentAsString().contains(user.getFirstName()));
        assert(response.getContentAsString().contains(user.getSurname()));
        assert(response.getContentAsString().contains(user.getPassword()));
    }

    @Test
    public void givenNoUser_whenFindAll_thenNotFound()
        throws Exception {

        MvcResult mvcResult = this.mockMvc.perform(get(USER_BASE_URI + "/pages/0")
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }


    @Test
    public void givenOneUnlockedUser_whenFindAllLocked_thenEmptyList()
        throws Exception {
        userRepository.save(user);

        MvcResult mvcResult = this.mockMvc.perform(get(USER_BASE_URI + "/access")
            .param("access", "false")
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        List<ApplicationUserDto> responseUserDtos = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
            ApplicationUserDto[].class));

        assertEquals(0, responseUserDtos.size());
    }

    @Test
    public void givenOneLockedUser_whenFindAllLocked_thenListWithSizeOneAndUserWithAllProperties()
        throws Exception {
        user.setAccess(false);
        userRepository.save(user);

        MvcResult mvcResult = this.mockMvc.perform(get(USER_BASE_URI + "/access")
            .param("access", "false")
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        List<ApplicationUserDto> responseUserDtos = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
            ApplicationUserDto[].class));


        assertEquals(1, responseUserDtos.size());
        ApplicationUserDto responseUserDto = responseUserDtos.get(0);
        assertAll(
            //don't assert ID because there were entries in the table before which got
            //deleted but it still affects the id.
            () -> assertEquals(user.getEmail(), responseUserDto.getEmail()),
            () -> assertEquals(user.getFirstName(), responseUserDto.getFirstName()),
            () -> assertEquals(user.getSurname(), responseUserDto.getSurname()),
            () -> assertEquals(user.getPassword(), responseUserDto.getPassword()),
            () -> assertEquals(user.getAdmin(), responseUserDto.isAdmin()),
            () -> assertEquals(user.getAccess(), responseUserDto.isAccess())
        );
    }

    @Test
    public void givenOneUser_whenFind_thenCorrectUser()
        throws Exception {
        userRepository.save(user);

        MvcResult mvcResult = this.mockMvc.perform(get(USER_BASE_URI + "/email")
            .param("email", user.getEmail()))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        ApplicationUserDto responseUserDto = objectMapper.readValue(response.getContentAsString(),
            ApplicationUserDto.class);
        assertAll(
            //don't assert ID because there were entries in the table before which got deleted
            //but it still affects the id.
            () -> assertEquals(user.getEmail(), responseUserDto.getEmail()),
            () -> assertEquals(user.getFirstName(), responseUserDto.getFirstName()),
            () -> assertEquals(user.getSurname(), responseUserDto.getSurname()),
            () -> assertEquals(user.getPassword(), responseUserDto.getPassword()),
            () -> assertEquals(user.getAdmin(), responseUserDto.isAdmin()),
            () -> assertEquals(user.getAccess(), responseUserDto.isAccess())
        );
    }

    @Test
    public void givenNothing_whenFind_thenNotFound()
        throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get(USER_BASE_URI + "/email")
            .param("email", user.getEmail()))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    @Test
    public void givenOneUser_whenSearchWithRightCriteria_thenCorrectUser()
        throws Exception {
        userRepository.save(user);

        MvcResult mvcResult = this.mockMvc.perform(get(USER_BASE_URI + "/search/pages/0")
            .param("criteria", "te")
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());


        assert(response.getContentAsString().contains(user.getEmail()));
        assert(response.getContentAsString().contains(user.getFirstName()));
        assert(response.getContentAsString().contains(user.getSurname()));
        assert(response.getContentAsString().contains(user.getPassword()));
    }

    @Test
    public void givenOneUser_whenSearchWithWrongCriteria_thenNotFound()
        throws Exception {
        userRepository.save(user);

        MvcResult mvcResult = this.mockMvc.perform(get(USER_BASE_URI + "/search/pages/0")
            .param("criteria", "xxx")
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }


    //**************
    //***  POST  ***
    //**************

    @Test
    public void givenNothing_whenCreateValidUser_thenUserCreatedSuccessfully() throws Exception {
        String body = objectMapper.writeValueAsString(userDto); // map userDto to json

        MvcResult mvcResult = this.mockMvc.perform(post(USER_BASE_URI + "/signUpUser")
            .contentType(MediaType.APPLICATION_JSON)
            .content(body)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andExpect(status().isCreated())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.CREATED.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        ApplicationUserDto responseUserDto = objectMapper.readValue(response.getContentAsString(),
            ApplicationUserDto.class);

        assertNotNull(responseUserDto.getId());
        assertEquals(userDto.getEmail(), responseUserDto.getEmail());
        assertEquals(userDto.getFirstName(), responseUserDto.getFirstName());
        assertEquals(userDto.getSurname(), responseUserDto.getSurname());
        assertEquals(userDto.isAccess(), responseUserDto.isAccess());
        assertEquals(userDto.isAdmin(), responseUserDto.isAdmin());
    }

    @Test
    public void givenNothing_whenCreateInvalidUser_thenValidationException() throws Exception {
        userDto.setPassword("");
        String body = objectMapper.writeValueAsString(userDto); // map userDto to json

        MvcResult mvcResult = this.mockMvc.perform(post(USER_BASE_URI + "/signUpUser")
            .contentType(MediaType.APPLICATION_JSON)
            .content(body)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus());
    }

    //***************
    //***  PATCH  ***
    //***************

    @Test
    public void givenOneLockedUser_whenPatch_thenUserAccessWillBeUnlocked() throws Exception {
        user.setAccess(false);
        userRepository.save(user);
        ApplicationUserDto userDto = userMapper.applicationUserToApplicationUserDto(user); // create test entity dto
        String body = objectMapper.writeValueAsString(userDto); // map userDto to json

        MvcResult mvcResult = this.mockMvc.perform(patch(USER_BASE_URI + "/unlock")
            .contentType(MediaType.APPLICATION_JSON)
            .content(body)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus()); //OK ?
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        ApplicationUserDto responseUserDto = objectMapper.readValue(response.getContentAsString(),
            ApplicationUserDto.class);

        assertNotNull(responseUserDto.getId());

        assertNotEquals(userDto.isAccess(), responseUserDto.isAccess());
        assertEquals(true, responseUserDto.isAccess());
    }

    @Test
    public void givenOneUser_whenPatch_thenUserAccessWillBeLocked() throws Exception {
        userRepository.save(user);
        ApplicationUserDto userDto = userMapper.applicationUserToApplicationUserDto(user); // create test entity dto
        String body = objectMapper.writeValueAsString(userDto); // map userDto to json

        MvcResult mvcResult = this.mockMvc.perform(patch(USER_BASE_URI + "/lock")
            .contentType(MediaType.APPLICATION_JSON)
            .content(body)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        ApplicationUserDto responseUserDto = objectMapper.readValue(response.getContentAsString(),
            ApplicationUserDto.class);

        assertNotNull(responseUserDto.getId());

        assertNotEquals(userDto.isAccess(), responseUserDto.isAccess());
        assertEquals(false, responseUserDto.isAccess());
    }


    /* *******Can not test because of Principal usage*******

    @Test
    public void givenValidEditedUserDto_whenPatch_thenUpdatedUserIsReturned() throws Exception {
        //Arrange
        user = userRepository.save(user);
        EditedUserDto editedUserDto = TestData.getTestEditedUserDtoWithId(user.getId());

        String body = objectMapper.writeValueAsString(editedUserDto);

        //Act
        MvcResult mvcResult = this.mockMvc.perform(patch(USER_BASE_URI + "/" + user.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(body)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        ApplicationUserDto responseUserDto = objectMapper.readValue(response.getContentAsString(),
            ApplicationUserDto.class);

        //Assert
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(responseUserDto.getId()).isEqualTo(editedUserDto.getId());
        softly.assertThat(responseUserDto.getEmail()).isEqualTo(editedUserDto.getEmail());
        softly.assertThat(responseUserDto.getFirstName()).isEqualTo(editedUserDto.getFirstName());
        softly.assertThat(responseUserDto.getSurname()).isEqualTo(editedUserDto.getSurname());
        softly.assertThat(encoder.matches(editedUserDto.getPassword(), responseUserDto.getPassword()));
        softly.assertThat(responseUserDto.isAdmin()).isEqualTo(editedUserDto.isAdmin());
        softly.assertThat(responseUserDto.isAccess()).isEqualTo(editedUserDto.isAccess());
        softly.assertAll();
    }

     */

    //***************
    //***  DELETE  ***
    //***************
/*

*******Can not test because of Principal usage*******

    @Test
    public void givenExistingUserId_whenDelete_thenUserIsDeleted() throws Exception {
        //Arrange
        user = userRepository.save(user);

        //Act
        MvcResult mvcResult = this.mockMvc.perform(delete(USER_BASE_URI + "/" + user.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        //Assert
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertFalse(userRepository.existsById(1L));
        assertEquals(0, userRepository.findAll().size());
    }

 */
}
