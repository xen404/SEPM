package at.ac.tuwien.sepm.groupphase.backend.integrationtest;


import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ApplicationUserDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.EventDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SimpleEventDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SimpleNewsDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.EventMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Artist;
import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import at.ac.tuwien.sepm.groupphase.backend.repository.EventRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.LocationRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.ShowRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.TicketRepository;
import at.ac.tuwien.sepm.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepm.groupphase.backend.service.impl.SimpleEventService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class EventEndpointTest implements TestData {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EventMapper eventMapper;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private ShowRepository showRepository;

    @Autowired
    private ShowRepository artistRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private SimpleEventService simpleEventService;

    private Event event;

    @BeforeEach
    public void beforeEach() {
        ticketRepository.deleteAllInBatch();
        showRepository.deleteAllInBatch();
        eventRepository.deleteAllInBatch();
        artistRepository.deleteAllInBatch();
        event = TestData.getTestEventWithId(1L);
    }

    private void checkEventsForEquality(EventDto eventDto1, EventDto eventDto2) {
        eventDto1.setId(null);
        eventDto2.setId(null);
        /*
        if (eventDto1.getArtists() != null) {
            eventDto1.getArtists().forEach(a -> a.setId(null));
            eventDto2.getArtists().forEach(a -> a.setId(null));
        }

         */
        if (eventDto1.getShows() != null) {
            eventDto1.getShows().forEach(a -> a.setId(null));
            eventDto2.getShows().forEach(a -> a.setId(null));
        }
        /*
        if (eventDto1.getNews() != null) {
            eventDto1.getNews().forEach(a -> a.setId(null));
            eventDto2.getNews().forEach(a -> a.setId(null));
        }

         */
        assertEquals(eventDto2, eventDto1);
    }

    private void checkEventsForEquality(Event event1, Event event2) {
        event1.setId(null);
        event2.setId(null);
        /*
        if (event1.getArtists() != null) {
            event1.getArtists().forEach(a -> a.setId(null));
            event2.getArtists().forEach(a -> a.setId(null));
        }

         */
        if (event1.getShows() != null) {
            event1.getShows().forEach(a -> a.setId(null));
            event2.getShows().forEach(a -> a.setId(null));
        }
        /*
        if (event1.getNews() != null) {
            event1.getNews().forEach(a -> a.setId(null));
            event2.getNews().forEach(a -> a.setId(null));
        }

         */
        assertEquals(event2, event1);
    }

    //*************
    //***  GET  ***
    //*************

    @Test
    public void givenNothing_whenFindByID_then404() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get(EVENT_BASE_URI + "/{id}", event.getId())
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());

    }

    @Test
    public void givenOneEvent_whenFindById_thenEventWithAllProperties() throws Exception {
        //locationRepository.save(TestData.getTestLocation());
        event.setShows(null);
        event.setArtists(null);
        event = eventRepository.save(event);

        MvcResult mvcResult = this.mockMvc.perform(get(EVENT_BASE_URI + "/{id}", event.getId())
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.getStatus())
        );

        EventDto eventDto = objectMapper.readValue(response.getContentAsString(),
            EventDto.class);

        eventDto.setShows(null);
        eventDto.setArtists(null);
        checkEventsForEquality(event, eventMapper.dtoToEntity(eventDto));
    }

    @Test
    public void givenNothing_whenFindSimple_then404() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get(EVENT_BASE_URI + "/{id}/simple", event.getId())
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());

    }

    @Test
    public void givenOneEvent_whenFindSimple_thenSimpleEventDtoWithCorrectProperties() throws Exception {
        //Arrange
        Event event1 = TestData.getTestEventWithId(1L);
        event1.setShows(null);
        event1.setArtists(null);
        event1 = eventRepository.save(event1);
        SimpleEventDto eventDto1 = eventMapper.eventToSimpleEventDto(event1);

        //Act
        MvcResult mvcResult = this.mockMvc.perform(get(EVENT_BASE_URI + "/{id}/simple", event1.getId())
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();


        //Assert
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        softly.assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
        softly.assertAll();

        SimpleEventDto eventDto = objectMapper.readValue(response.getContentAsString(), SimpleEventDto.class);
        eventDto1.setArtists(new ArrayList<>());

        softly = new SoftAssertions();
        softly.assertThat(eventDto).isEqualTo(eventDto1);
        softly.assertAll();
    }

    @Test
    public void givenNothing_whenFindAllSimple_thenReturnsEmptyList() throws Exception {
        //Arrange

        //Act
        MvcResult mvcResult = this.mockMvc.perform(get(EVENT_BASE_URI + "/simple")
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        //Assert
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        softly.assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
        softly.assertAll();

        List<SimpleEventDto> eventDtos = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
            SimpleEventDto[].class));

        assertThat(eventDtos.size()).isEqualTo(0);
    }

    @Test
    public void givenTwoEvents_whenFindAllSimple_thenListOfCorrectSimpleEventDtos() throws Exception {
        //Arrange
        Event event1 = TestData.getTestEventWithId(1L);
        event1.setShows(null);
        event1.setArtists(new ArrayList<>());
        event1 = eventRepository.save(event1);
        Event event2 = TestData.getTestEventWithId(2L);
        event2.setShows(null);
        event2.setArtists(new ArrayList<>());
        event2 = eventRepository.save(event2);
        List<Event> events = new ArrayList<>();
        events.add(event1);
        events.add(event2);
        List<SimpleEventDto> testDataList = eventMapper.eventToSimpleEventDto(events);

        //Act
        MvcResult mvcResult = this.mockMvc.perform(get(EVENT_BASE_URI + "/simple")
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();


        //Assert
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        softly.assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
        softly.assertAll();

        List<SimpleEventDto> eventDtos = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
            SimpleEventDto[].class));

        softly = new SoftAssertions();
        softly.assertThat(eventDtos.size()).isEqualTo(2);
        softly.assertThat(eventDtos).containsOnlyElementsOf(testDataList);
        softly.assertAll();
    }


    //**************
    //***  POST  ***
    //**************

    @Test
    public void givenNothing_whenCreateValidEvent_thenEventCreatedSuccessfully() throws Exception {
        //locationRepository.save(TestData.getTestLocation());
        //simpleEventService.saveEvent(event);
        // showRepository.save(TestData.getTestShow());
        event.setShows(null);
        event.setArtists(null);
        EventDto eventDto = eventMapper.entityToDto(event); // create test entity dto
        String body = objectMapper.writeValueAsString(eventDto); // map eventDto to json

        MvcResult mvcResult = this.mockMvc.perform(post(EVENT_BASE_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .content(body)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.CREATED.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        EventDto responseEventDto = objectMapper.readValue(response.getContentAsString(),
            EventDto.class);

        assertNotNull(responseEventDto.getId());
        //Set generated properties to null to make the response comparable with the original input
        checkEventsForEquality(eventDto, responseEventDto);
    }

    @Test
    public void givenNothing_whenCreateTitleNullEvent_then400() throws Exception {
        EventDto eventDto = eventMapper.entityToDto(event); // create test entity dto
        eventDto.setTitle(null);
        String body = objectMapper.writeValueAsString(eventDto); // map eventDto to json


        MvcResult mvcResult = this.mockMvc.perform(post(EVENT_BASE_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .content(body)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus()),
            () -> {
                //Reads the errors from the body
                String content = response.getContentAsString();
                content = content.substring(content.indexOf('[') + 1, content.indexOf(']'));
                String[] errors = content.split(",");
                assertEquals(1, errors.length);
            }
        );
    }

    @Test
    public void givenNothing_whenCategoryNullEvent_then400() throws Exception {
        EventDto eventDto = eventMapper.entityToDto(event); // create test entity dto
        eventDto.setCategory(null);
        String body = objectMapper.writeValueAsString(eventDto); // map eventDto to json


        MvcResult mvcResult = this.mockMvc.perform(post(EVENT_BASE_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .content(body)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus()),
            () -> {
                //Reads the errors from the body
                String content = response.getContentAsString();
                content = content.substring(content.indexOf('[') + 1, content.indexOf(']'));
                String[] errors = content.split(",");
                assertEquals(1, errors.length);
            }
        );
    }

    /* Artists
    @Test
    public void givenNothing_whenArtistsNullEvent_then400() throws Exception {
        EventDto eventDto = eventMapper.entityToDto(event); // create test entity dto
        eventDto.setShows(null); //Event should be saved before show
        eventDto.setArtists(null); // Test case
        String body = objectMapper.writeValueAsString(eventDto); // map eventDto to json


        MvcResult mvcResult = this.mockMvc.perform(post(EVENT_BASE_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .content(body)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus()),
            () -> {
                //Reads the errors from the body
                String content = response.getContentAsString();
                content = content.substring(content.indexOf('[') + 1, content.indexOf(']'));
                String[] errors = content.split(",");
                assertEquals(1, errors.length);
            }
        );
    }

     */

    @Test
    public void givenNothing_whenImagesNullShowsNullArtistsNullEvent_then201() throws Exception {
        EventDto eventDto = eventMapper.entityToDto(event); // create test entity dto
        eventDto.setImage(null);
        eventDto.setShows(null);
        eventDto.setArtists(null);
        String body = objectMapper.writeValueAsString(eventDto); // map eventDto to json


        MvcResult mvcResult = this.mockMvc.perform(post(EVENT_BASE_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .content(body)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.CREATED.value(), response.getStatus())
        );
    }

    @Test
    public void givenNothing_whenDescriptionNullEvent_then400()
        throws Exception {
        EventDto eventDto = eventMapper.entityToDto(event); // create test entity dto
        eventDto.setDescription(null);
        String body = objectMapper.writeValueAsString(eventDto); // map eventDto to json


        MvcResult mvcResult = this.mockMvc.perform(post(EVENT_BASE_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .content(body)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus()),
            () -> {
                //Reads the errors from the body
                String content = response.getContentAsString();
                content = content.substring(content.indexOf('[') + 1, content.indexOf(']'));
                String[] errors = content.split(",");
                assertEquals(1, errors.length);
            }
        );
    }


    @Test
    public void givenNothing_whenFindByLocation_ThenLocationNotInResponse()
        throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get(SHOW_BASE_URI + "/location")
            .param("id", "1")
            .param("page", "0")
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        assert (!response.getContentAsString().contains("location"));
    }

    @Test
    public void givenOneEvent_whenFindAll_thenReturnEvent()
        throws Exception {
        Event event1 = TestData.getTestEventWithId(1L);
        event1.setShows(null);
        event1.setArtists(new ArrayList<>());
        event1 = eventRepository.save(event1);

        MvcResult mvcResult = this.mockMvc.perform(get(EVENT_BASE_URI + "/pagination")
            .param("page", "0")
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        assert (response.getContentAsString().contains(event1.getTitle()));
        assert (response.getContentAsString().contains(event1.getDescription()));
    }

    @Test
    public void givenNoEvent_whenFindAll_thenNotFound()
        throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get(EVENT_BASE_URI + "/pagination")
            .param("page", "0")
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    @Test
    public void givenOneEvent_whenSearchWithRightCriteria_thenCorrectEvent()
        throws Exception {
        Event event1 = TestData.getTestEventWithId(1L);
        event1.setShows(null);
        event1.setArtists(new ArrayList<>());
        event1 = eventRepository.save(event1);

        MvcResult mvcResult = this.mockMvc.perform(get(EVENT_BASE_URI + "/search")
            .param("categoryId", "3")
            .param("duration", "120")
            .param("title", "Cool")
            .param("page", "0")
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        assert (response.getContentAsString().contains(event1.getTitle()));
        assert (response.getContentAsString().contains(event1.getDescription()));
    }

    @Test
    public void givenOneEvent_whenSearchWithWrongCriteria_thenNotFound()
        throws Exception {
        Event event1 = TestData.getTestEventWithId(1L);
        event1.setShows(null);
        event1.setArtists(new ArrayList<>());
        event1 = eventRepository.save(event1);

        MvcResult mvcResult = this.mockMvc.perform(get(EVENT_BASE_URI + "/search")
            .param("categoryId", "2")
            .param("duration", "120")
            .param("title", "Cool")
            .param("page", "0")
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    /*
    @Test
    public void givenNothing_whenShowsNullEvent_then400() throws Exception {
        EventDto eventDto = eventMapper.entityToDto(event); // create test entity dto
        eventDto.setShows(null);
        String body = objectMapper.writeValueAsString(eventDto); // map eventDto to json


        MvcResult mvcResult = this.mockMvc.perform(post(EVENT_BASE_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .content(body)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus()),
            () -> {
                //Reads the errors from the body
                String content = response.getContentAsString();
                content = content.substring(content.indexOf('[') + 1, content.indexOf(']'));
                String[] errors = content.split(",");
                assertEquals(2, errors.length);
            }
        );
    }

     */


}
