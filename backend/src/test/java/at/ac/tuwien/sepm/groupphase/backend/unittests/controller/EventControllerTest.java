package at.ac.tuwien.sepm.groupphase.backend.unittests.controller;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.EventEndpoint;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SimpleEventDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.EventMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Artist;
import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import at.ac.tuwien.sepm.groupphase.backend.service.EventService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = EventEndpoint.class)
@ActiveProfiles("testSecurityOff")
@ComponentScan(basePackages = "at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper")
public class EventControllerTest implements TestData {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EventMapper eventMapper;

    @MockBean
    private EventService eventService;

    @Test
    @WithMockUser
    public void givenOneEvent_whenFindSimple_thenSimpleEventDtoWithCorrectProperties() throws Exception {
        //Arrange
        Event event1 = TestData.getTestEventWithId(1L);
        event1.setShows(null);
        List<Artist> artists = new ArrayList<>();
        Artist artist = TestData.getTestArtistWithId(1L);
        artist.setEvents(new ArrayList<>(){
            {
                add(event1);
            }
        });
        event1.setArtists(artists);
        when(eventService.findOne(event1.getId())).thenReturn(event1);
        SimpleEventDto eventDto1 = eventMapper.eventToSimpleEventDto(event1);

        //Act
        MvcResult mvcResult = this.mockMvc.perform(get(EVENT_BASE_URI + "/{id}/simple", event1.getId()))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        //Assert
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        softly.assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
        softly.assertAll();

        SimpleEventDto eventDto = objectMapper.readValue(response.getContentAsString(), SimpleEventDto.class);

        softly = new SoftAssertions();
        softly.assertThat(eventDto).isEqualTo(eventDto1);
        softly.assertAll();
    }

    @Test
    @WithMockUser
    public void givenNothing_whenFindAllSimple_thenReturnsEmptyList() throws Exception {
        //Arrange
        when(eventService.findAll()).thenReturn(new ArrayList<>());

        //Act
        MvcResult mvcResult = this.mockMvc.perform(get(EVENT_BASE_URI + "/simple"))
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
    @WithMockUser
    public void givenTwoEvents_whenFindAllSimple_thenListOfCorrectSimpleEventDtos() throws Exception {
        //Arrange
        Event event1 = TestData.getTestEventWithId(1L);
        event1.setShows(null);
        event1.setArtists(new ArrayList<>());
        Event event2 = TestData.getTestEventWithId(2L);
        event2.setShows(null);
        event2.setArtists(new ArrayList<>());
        List<Event> events = new ArrayList<>();
        when(eventService.findAll()).thenReturn(events);
        events.add(event1);
        events.add(event2);
        List<SimpleEventDto> testDataList = eventMapper.eventToSimpleEventDto(events);

        //Act
        MvcResult mvcResult = this.mockMvc.perform(get(EVENT_BASE_URI + "/simple"))
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
}
