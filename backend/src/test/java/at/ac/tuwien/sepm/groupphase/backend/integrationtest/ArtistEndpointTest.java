package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.*;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.ArtistMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import at.ac.tuwien.sepm.groupphase.backend.repository.*;
import at.ac.tuwien.sepm.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepm.groupphase.backend.service.impl.SimpleEventService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class ArtistEndpointTest implements TestData {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ShowRepository showRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ArtistMapper artistMapper;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private SecurityProperties securityProperties;

    private Artist artist;

    @BeforeEach
    public void beforeEach() {
        ticketRepository.deleteAllInBatch();
        showRepository.deleteAllInBatch();
        artistRepository.deleteAllInBatch();
        eventRepository.deleteAllInBatch();

        artist = TestData.getTestArtist();
    }

    //*************
    //***  GET  ***
    //*************

    @Test
    public void givenNothing_whenFindByID_then404() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get(ARTIST_BASE_URI + "/{id}", artist.getId())
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }


    @Test
    public void givenOneArtist_whenFindById_thenArtistWithAllProperties() throws Exception {
        Event event = TestData.getTestEvent();
        event.setArtists(null);
        event.setShows(null);
        Event savedEvent = eventRepository.save(event);

        artist.getEvents().clear();
        artist.getEvents().add(savedEvent); //Update event List after setting artists and shows to null

        artist = artistRepository.save(artist);

        MvcResult mvcResult = this.mockMvc.perform(get(ARTIST_BASE_URI + "/{id}", artist.getId())
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        softly.assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
        softly.assertAll();

        ArtistDto artistDto = objectMapper.readValue(response.getContentAsString(),
            ArtistDto.class);

        softly = new SoftAssertions();
        softly.assertThat(artistDto.getId()).isEqualTo(artist.getId());
        softly.assertThat(artistDto.getName()).isEqualTo(artist.getName());
        softly.assertThat(artistDto.getEventIds().size()).isEqualTo(1);
        softly.assertThat(artistDto.getEventIds()).containsOnlyElementsOf(new ArrayList<>(){{add(savedEvent.getId());}});
        softly.assertAll();
    }

    @Test
    public void givenOneArtist_whenFindAll_thenReturnArtist()
        throws Exception {
        Event event = TestData.getTestEvent();
        event.setArtists(null);
        event.setShows(null);
        Event savedEvent = eventRepository.save(event);

        artist.getEvents().clear();
        artist.getEvents().add(savedEvent); //Update event List after setting artists and shows to null

        artist = artistRepository.save(artist);

        MvcResult mvcResult = this.mockMvc.perform(get(ARTIST_BASE_URI + "/pagination")
            .param("page", "0")
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        assert(response.getContentAsString().contains(artist.getName()));
    }

    @Test
    public void givenNoArtist_whenFindAll_thenNotFound()
        throws Exception {

        MvcResult mvcResult = this.mockMvc.perform(get(ARTIST_BASE_URI + "/pagination")
            .param("page", "0")
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    @Test
    public void givenOneArtist_whenSearchWithRightCriteria_thenCorrectArtist()
        throws Exception {
        Event event = TestData.getTestEvent();
        event.setArtists(null);
        event.setShows(null);
        Event savedEvent = eventRepository.save(event);

        artist.getEvents().clear();
        artist.getEvents().add(savedEvent); //Update event List after setting artists and shows to null

        artist = artistRepository.save(artist);

        MvcResult mvcResult = this.mockMvc.perform(get(ARTIST_BASE_URI + "/search")
            .param("name", "Pickles")
            .param("page", "0")
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        assert(response.getContentAsString().contains(artist.getName()));
    }

    @Test
    public void givenNoArtist_whenSearchWithRightCriteria_thenNotFound()
        throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get(ARTIST_BASE_URI + "/search")
            .param("name", "Pickles")
            .param("page", "0")
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

}
