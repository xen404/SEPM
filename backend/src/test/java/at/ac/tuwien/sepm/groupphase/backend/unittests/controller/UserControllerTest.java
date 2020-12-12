package at.ac.tuwien.sepm.groupphase.backend.unittests.controller;


import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.UserEndpoint;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ApplicationUserDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.UserMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.List;

import static at.ac.tuwien.sepm.groupphase.backend.basetest.TestData.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = UserEndpoint.class)
@ActiveProfiles("testSecurityOff")
@ComponentScan(basePackages = "at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper")
public class UserControllerTest implements TestData {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserMapper userMapper;

    @MockBean
    private UserService userService;

    /*@Test
    @WithMockUser
    public void givenSomeUsers_whenFindAll_thenReturns200AndCorrectAmountOfUsers() throws Exception {
        //Arrange
        Pageable pageable = PageRequest.of(0, 30);
        List<ApplicationUser> nRandomUsers= TestData.getNRandomUsers(5);
        Page<ApplicationUser> page = new PageImpl<>(nRandomUsers);
        when(userService.findAll(pageable)).thenReturn(page);

        //Act & Assert
        MvcResult mvcResult = mockMvc.perform(get(USER_BASE_URI))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        Page<ApplicationUserDto> userDtos = objectMapper.readValue(response.getContentAsString(),
            Page.class);

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(userDtos.getTotalElements()).isEqualTo(5);
    }*/

    @Test
    @WithMockUser
    public void givenOneUser_whenFind_thenReturns200AndCorrectUser() throws Exception {
        //Arrange
        ApplicationUser applicationUser = userMapper.applicationUserDtoToApplicationUser(TestData.getTestUserDto());
        when(userService.findUserByEmail(applicationUser.getEmail())).thenReturn(applicationUser);

        //Act & Assert
        MvcResult mvcResult = mockMvc.perform(get(USER_BASE_URI + "/email")
            .param("email", applicationUser.getEmail()))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        ApplicationUserDto userDto = objectMapper.readValue(response.getContentAsString(),
            ApplicationUserDto.class);

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(userDto).isEqualTo(TestData.getTestUserDto());
    }
}
