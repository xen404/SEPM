package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.LocationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SeatDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ShowDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ShowSeatDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.EventMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.LocationMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import at.ac.tuwien.sepm.groupphase.backend.entity.Location;
import at.ac.tuwien.sepm.groupphase.backend.entity.Seat;
import at.ac.tuwien.sepm.groupphase.backend.repository.EventRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.LocationRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.SeatRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.ShowRepository;
import at.ac.tuwien.sepm.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepm.groupphase.backend.service.impl.SimpleEventService;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class LocationEndpointTest implements TestData {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LocationMapper locationMapper;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private SeatRepository seatRepository;

    private Location location;

    @BeforeEach
    public void beforeEach() {
        seatRepository.deleteAllInBatch();
        locationRepository.deleteAllInBatch();
        location = TestData.getTestLocation();
    }

    @Test
    public void givenNothing_whenCreateValidLocation_thenLocationCreatedSuccessfully() throws Exception {
        // locationRepository.save(TestData.getTestLocation());
        //simpleEventService.saveEvent(event);
        // showRepository.save(TestData.getTestShow());
        //event.setShows(null);
        //Event event = TestData.getTestEvent();
        //event.setShows(null);
        //simpleEventService.saveEvent(event);

        location.setShows(null);
        LocationDto locationDto = locationMapper.locationToLocationDto(location); // create test entity dto
        String body = objectMapper.writeValueAsString(locationDto); // map s to json

        MvcResult mvcResult = this.mockMvc.perform(post(LOCATION_BASE_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .content(body)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.CREATED.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        LocationDto responseLocationDto = objectMapper.readValue(response.getContentAsString(),
            LocationDto.class);

        assertNotNull(responseLocationDto.getId());
        //Set generated properties to null to make the response comparable with the original input
        locationDto.setId(responseLocationDto.getId());
        //checkEventsForEquality(locationDto, responseLocationDto);
        assertEquals(locationDto, responseLocationDto);
    }

    @Test
    public void givenLocationWithSomeSeats_whenGetSeats_thenListOfCorrectSeats() throws Exception {
        //Arrange
        location.setShows(null);
        location = locationRepository.saveAndFlush(location);
        Seat seat1 = TestData.getTestSeatWithId(null);
        seat1.setLocation(location);
        Seat seat2 = TestData.getTestSeatWithId(null);
        seat2.setLocation(location);
        Seat seat3 = TestData.getTestSeatWithId(null);
        Location location2 = TestData.getTestLocationWithId(null);
        location2.setShows(null);
        location2 = locationRepository.saveAndFlush(location2);
        seat3.setLocation(location2);
        seat1 = seatRepository.saveAndFlush(seat1);
        seat2 = seatRepository.saveAndFlush(seat2);
        seat3 = seatRepository.saveAndFlush(seat3);
        List<SeatDto> testDataList = new ArrayList<>();
        testDataList.add(locationMapper.seatToSeatDto(seat1));
        testDataList.add(locationMapper.seatToSeatDto(seat2));

        //Act
        MvcResult mvcResult = this.mockMvc.perform(get(LOCATION_BASE_URI + "/{id}/seats", location.getId())
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        //Assert
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        softly.assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
        softly.assertAll();

        List<SeatDto> seatDtos = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
            SeatDto[].class));

        assertThat(seatDtos).containsOnlyElementsOf(testDataList);
    }

    @Test
    public void givenNothing_whenGetSeats_then404() throws Exception {
        //Arrange


        //Act
        MvcResult mvcResult = this.mockMvc.perform(get(LOCATION_BASE_URI + "/{id}/seats", location.getId())
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        //Assert
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void givenOneLocation_whenFindAll_thenReturnLocation()
        throws Exception {
        location.setShows(null);
        location = locationRepository.saveAndFlush(location);

        MvcResult mvcResult = this.mockMvc.perform(get(LOCATION_BASE_URI + "/pagination")
            .param("page", "0")
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        assert(response.getContentAsString().contains(location.getDescription()));
    }

    @Test
    public void givenNoLocation_whenFindAll_thenNotFound()
        throws Exception {

        MvcResult mvcResult = this.mockMvc.perform(get(LOCATION_BASE_URI + "/pagination")
            .param("page", "0")
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }


}
