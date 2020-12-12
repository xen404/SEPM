package at.ac.tuwien.sepm.groupphase.backend.unittests.controller;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.ArtistEndpoint;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ArtistDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.ArtistMapper;
import at.ac.tuwien.sepm.groupphase.backend.service.ArtistService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = ArtistEndpoint.class)
@ActiveProfiles("testSecurityOff")
@ComponentScan(basePackages = "at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper")
public class ArtistControllerTest implements TestData {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ArtistMapper artistMapper;

    @MockBean
    private ArtistService artistService;

    @Test
    @WithMockUser
    public void givenSomeArtists_whenFindAll_thenReturns200AndCorrectAmountOfArtists() throws Exception {
        //Arrange
        when(artistService.findAllByOrderByName()).thenReturn(TestData.getNRandomArtists(5));

        //Act & Assert
        MvcResult mvcResult = mockMvc.perform(get(ARTIST_BASE_URI))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        List<ArtistDto> artistDtos = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
            ArtistDto[].class));

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(artistDtos.size()).isEqualTo(5);
    }
}
