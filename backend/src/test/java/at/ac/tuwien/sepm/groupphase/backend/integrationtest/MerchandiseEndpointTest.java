package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.*;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.LocationMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.MerchandiseMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.ShowMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import at.ac.tuwien.sepm.groupphase.backend.repository.*;
import at.ac.tuwien.sepm.groupphase.backend.security.JwtTokenizer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.SoftAssertions;
import org.junit.Ignore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class MerchandiseEndpointTest implements TestData {

    @Autowired
    private MockMvc mockMvc;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private MerchandiseRepository merchandiseRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MerchandiseMapper merchandiseMapper;

    @Autowired
    private LocationMapper locationMapper;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private SecurityProperties securityProperties;

    @BeforeEach
    public void beforeEach() {
        merchandiseRepository.deleteAllInBatch();
        eventRepository.deleteAllInBatch();
    }

    @Test
    @WithMockUser(ADMIN_USER)
    public void givenNothing_whenCreateMerchandise_then201AndMerchandiseSaved() throws Exception {
        //Arrange
        Event event = TestData.getTestEventWithId(null);
        event.setArtists(new ArrayList<>());
        event.setShows(new ArrayList<>());
        event = eventRepository.saveAndFlush(event);
        MerchandiseDto merchandiseDto = TestData.getMerchandiseDto();
        merchandiseDto.setEventId(event.getId());
        String body = objectMapper.writeValueAsString(merchandiseDto);

        //Act
        MvcResult mvcResult = this.mockMvc.perform(post(MERCHANDISE_BASE_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .content(body)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        //Assert
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
        softly.assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
        softly.assertAll();

        MerchandiseDto savedMerchandiseDto = objectMapper.readValue(response.getContentAsString(), MerchandiseDto.class);

        softly = new SoftAssertions();
        softly.assertThat(savedMerchandiseDto.getTitle()).isEqualTo(merchandiseDto.getTitle());
        softly.assertThat(merchandiseRepository.findById(savedMerchandiseDto.getId())).isNotEmpty();
        softly.assertAll();
    }

    @Test
    public void givenSomeMerchandise_whenGetMerchandiseForEvent_thenFindCorrectMerchandiseList() throws Exception {
        //Arrange
        Event event = TestData.getTestEventWithId(null);
        event.setArtists(new ArrayList<>());
        event.setShows(new ArrayList<>());
        event = eventRepository.saveAndFlush(event);
        Merchandise merchandise1 = TestData.getMerchandiseWithId(null);
        merchandise1.setEvent(event);
        merchandise1 = merchandiseRepository.saveAndFlush(merchandise1);
        Merchandise merchandise2 = TestData.getMerchandiseWithId(null);
        merchandise2.setEvent(event);
        merchandise2 = merchandiseRepository.saveAndFlush(merchandise2);
        MerchandiseDto merchandiseDto1 = merchandiseMapper.merchandiseToMerchandiseDto(merchandise1);
        MerchandiseDto merchandiseDto2 = merchandiseMapper.merchandiseToMerchandiseDto(merchandise2);
        List<MerchandiseDto> testList = new ArrayList<>();
        testList.add(merchandiseDto1);
        testList.add(merchandiseDto2);

        //Act
        MvcResult mvcResult = this.mockMvc.perform(get(MERCHANDISE_BASE_URI + "/event/{eventId}", event.getId())
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        //Assert
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        softly.assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
        softly.assertAll();

        List<MerchandiseDto> merchandiseDtos = Arrays.asList(objectMapper.readValue(response.getContentAsString(), MerchandiseDto[].class));

        softly = new SoftAssertions();
        softly.assertThat(merchandiseDtos.size()).isEqualTo(2);
        softly.assertThat(merchandiseDtos).containsOnlyElementsOf(testList);
        softly.assertAll();
    }

    @Test
    public void givenOneMerchandise_whenGetMerchandiseById_thenFindCorrectMerchandise() throws Exception {
        //Arrange
        Event event = TestData.getTestEventWithId(null);
        event.setArtists(new ArrayList<>());
        event.setShows(new ArrayList<>());
        event = eventRepository.saveAndFlush(event);
        Merchandise merchandise = TestData.getMerchandiseWithId(null);
        merchandise.setEvent(event);
        merchandise = merchandiseRepository.saveAndFlush(merchandise);

        //Act
        MvcResult mvcResult = this.mockMvc.perform(get(MERCHANDISE_BASE_URI + "/{id}", merchandise.getId())
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        //Assert
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        softly.assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
        softly.assertAll();

        MerchandiseDto merchandiseDto = objectMapper.readValue(response.getContentAsString(), MerchandiseDto.class);

        softly = new SoftAssertions();
        softly.assertThat(merchandiseDto.getId()).isEqualTo(merchandise.getId());
        softly.assertThat(merchandiseDto.getTitle()).isEqualTo(merchandise.getTitle());
        softly.assertThat(merchandiseDto.getEventId()).isEqualTo(merchandise.getEvent().getId());
        softly.assertThat(merchandiseDto.getBonusPoints()).isEqualTo(merchandise.getBonusPoints());
        softly.assertAll();
    }
}

