package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.datagenerator.AEventDataGenerator;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.ShowEndpoint;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.*;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.LocationMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.ShowMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import at.ac.tuwien.sepm.groupphase.backend.repository.*;
import at.ac.tuwien.sepm.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepm.groupphase.backend.service.impl.SimpleEventService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.SoftAssertions;
import org.junit.Ignore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
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

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class ShowEndpointTest implements TestData {

    @Autowired
    private MockMvc mockMvc;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private ShowRepository showRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    ArtistRepository artistRepository;

    @Autowired
    MerchandiseRepository merchandiseRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ShowMapper showMapper;

    @Autowired
    private LocationMapper locationMapper;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private SecurityProperties securityProperties;

    private Show show;

    @BeforeEach
    public void beforeEach() {
        merchandiseRepository.deleteAllInBatch();
        ticketRepository.deleteAllInBatch();
        usersRepository.deleteAllInBatch();
        seatRepository.deleteAllInBatch();
        showRepository.deleteAllInBatch();
        eventRepository.deleteAllInBatch();
        locationRepository.deleteAllInBatch();

        show = TestData.getTestShow();
        show.getLocation().setShows(null);
        show.getLocation().setSeats(null);
        show.getEvent().setShows(null);
        show.getEvent().setArtists(null);
    }

    public Show saveShow() {
        Location savedLocation = locationRepository.saveAndFlush(show.getLocation());
        Event savedEvent = eventRepository.saveAndFlush(show.getEvent());
        show.setEvent(savedEvent);
        show.setLocation(savedLocation);
        return showRepository.saveAndFlush(this.show);
    }


    @Test
    public void givenOneShow_whenGetSeats_thenListOfSeatDtosWithCorrectProperties() throws Exception {
        //Arrange
        Show show = saveShow();

        Seat seat1 = TestData.getTestSeatWithId(null);
        seat1.setRowNr(1);
        seat1.setSeatNr(2);
        seat1.setLocation(show.getLocation());
        Seat seat2 = TestData.getTestSeatWithId(null);
        seat2.setRowNr(11);
        seat2.setSeatNr(12);
        seat2.setLocation(show.getLocation());
        Seat seat3 = TestData.getTestSeatWithId(null);
        seat3.setRealSeat(false);
        seat3.setLocation(show.getLocation());
        seat1 = seatRepository.saveAndFlush(seat1);
        seat2 = seatRepository.saveAndFlush(seat2);
        seat3 = seatRepository.saveAndFlush(seat3);
        ApplicationUser user = TestData.getTestUser();
        user = usersRepository.saveAndFlush(user);
        Ticket ticket1 = TestData.getTestTicketWithId(null);
        ticket1.setUser(user);
        ticket1.setStatus(Ticket.Status.PURCHASED);
        ticket1.setShow(show);
        ticket1.setSeat(seat1);
        ticket1.setSector('A');
        ticket1.setPrice(100.90f);
        Ticket ticket2 = TestData.getTestTicketWithId(null);
        ticket2.setStatus(Ticket.Status.FREE);
        ticket2.setShow(show);
        ticket2.setSeat(seat2);
        ticket2.setSector('B');
        ticket2.setPrice(80.90f);
        ticketRepository.saveAndFlush(ticket1);
        ticketRepository.saveAndFlush(ticket2);
        List<ShowSeatDto> testDataList = new ArrayList<>();
        testDataList.add(new ShowSeatDto(seat1.getId(), ticket1.getSector(), seat1.getRowNr(), seat1.getSeatNr(), "PURCHASED", ticket1.getPrice()));
        testDataList.add(new ShowSeatDto(seat2.getId(), ticket2.getSector(), seat2.getRowNr(), seat2.getSeatNr(), "FREE", ticket2.getPrice()));
        testDataList.add(new ShowSeatDto(seat3.getId(), '-', seat3.getRowNr(), seat3.getSeatNr(), "NOSEAT", 0));

        //Act
        MvcResult mvcResult = mockMvc.perform(get(SHOW_BASE_URI + "/{id}/seats", show.getId())
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        //Assert
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        softly.assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
        softly.assertAll();

        List<ShowSeatDto> seatShowDtos = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
            ShowSeatDto[].class));

        softly = new SoftAssertions();
        softly.assertThat(seatShowDtos.size()).isEqualTo(3);
        softly.assertThat(seatShowDtos).containsOnlyElementsOf(testDataList);
        softly.assertAll();
    }

    @Test
    public void givenNothing_whenGetSeats_then404() throws Exception {
        //Arrange

        //Act
        MvcResult mvcResult = this.mockMvc.perform(get(SHOW_BASE_URI + "/{id}/seats", ID)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        //Assert
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void givenOneShow_whenFind_thenShowDtoHasCorrectProperties() throws Exception {
        //Arrange
        Show show = saveShow();

        //Act
        MvcResult mvcResult = this.mockMvc.perform(get(SHOW_BASE_URI + "/{id}", show.getId())
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        //Assert
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        softly.assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
        softly.assertAll();

        ShowDto showDto = objectMapper.readValue(response.getContentAsString(), ShowDto.class);

        softly = new SoftAssertions();
        softly.assertThat(showDto.getId()).isEqualTo(show.getId());
        softly.assertThat(showDto.getTitle()).isEqualTo(show.getTitle());
        softly.assertThat(showDto.getLocation().getId()).isEqualTo(show.getLocation().getId());
        softly.assertThat(showDto.getDescription()).isEqualTo(show.getDescription());
        softly.assertThat(showDto.getStartTime()).isEqualTo(show.getStartTime());
        softly.assertThat(showDto.getEndTime()).isEqualTo(show.getEndTime());
        softly.assertThat(showDto.getEventId()).isEqualTo(show.getEvent().getId());
        softly.assertAll();
    }

    @Test
    public void givenNothing_whenFind_then404() throws Exception {
        //Arrange

        //Act
        MvcResult mvcResult = this.mockMvc.perform(get(SHOW_BASE_URI + "/{id}", ID)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        //Assert
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void givenOneShow_whenGetPrices_thenListOfPriceDtosWithCorrectProperties() throws Exception {
        //Arrange
        Show show = saveShow();

        Seat seat1 = TestData.getTestSeatWithId(null);
        seat1.setLocation(show.getLocation());
        Seat seat2 = TestData.getTestSeatWithId(null);
        seat2.setLocation(show.getLocation());
        Seat seat3 = TestData.getTestSeatWithId(null);
        seat3.setLocation(show.getLocation());
        Seat seat4 = TestData.getTestSeatWithId(null);
        seat4.setLocation(show.getLocation());
        seat1 = seatRepository.saveAndFlush(seat1);
        seat2 = seatRepository.saveAndFlush(seat2);
        seat3 = seatRepository.saveAndFlush(seat3);
        seat4 = seatRepository.saveAndFlush(seat4);
        Ticket ticket1 = TestData.getTestTicketWithId(null);
        ticket1.setSector('0');
        ticket1.setPrice(100.60f);
        ticket1.setSeat(seat1);
        ticket1.setUser(null);
        ticket1.setShow(show);
        Ticket ticket2 = TestData.getTestTicketWithId(null);
        ticket2.setSector('0');
        ticket2.setPrice(100.60f);
        ticket2.setSeat(seat2);
        ticket2.setUser(null);
        ticket2.setShow(show);
        Ticket ticket3 = TestData.getTestTicketWithId(null);
        ticket3.setSector('A');
        ticket3.setPrice(89.90f);
        ticket3.setSeat(seat3);
        ticket3.setUser(null);
        ticket3.setShow(show);
        Ticket ticket4 = TestData.getTestTicketWithId(null);
        ticket4.setSector('B');
        ticket4.setPrice(50.80f);
        ticket4.setSeat(seat4);
        ticket4.setUser(null);
        ticket4.setShow(show);
        ticket1 = ticketRepository.saveAndFlush(ticket1);
        ticket2 = ticketRepository.saveAndFlush(ticket2);
        ticket3 = ticketRepository.saveAndFlush(ticket3);
        ticket4 = ticketRepository.saveAndFlush(ticket4);
        List<PriceDto> testDataList = new ArrayList<>();
        testDataList.add(new PriceDto(ticket1.getSector(), ticket2.getPrice()));
        testDataList.add(new PriceDto(ticket3.getSector(), ticket3.getPrice()));
        testDataList.add(new PriceDto(ticket4.getSector(), ticket4.getPrice()));

        //Act
        MvcResult mvcResult = this.mockMvc.perform(get(SHOW_BASE_URI + "/{id}/prices", show.getId())
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        //Assert
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        softly.assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
        softly.assertAll();

        List<PriceDto> priceDtos = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
            PriceDto[].class));

        softly = new SoftAssertions();
        softly.assertThat(priceDtos.size()).isEqualTo(3);
        softly.assertThat(priceDtos).containsOnlyElementsOf(testDataList);
        softly.assertAll();
    }

    @Test
    public void givenNothing_whenGetPrices_then404() throws Exception {
        //Arrange

        //Act
        MvcResult mvcResult = this.mockMvc.perform(get(SHOW_BASE_URI + "/{id}/prices", ID)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        //Assert
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void givenNothing_whenSaveWithValidProperties_then201AndShowAndTicketsSaved() throws Exception {
        //Arrange
        Event event = TestData.getTestEvent();
        event.setArtists(null);
        event.setShows(null);
        event = eventRepository.saveAndFlush(event);
        Location location = TestData.getTestLocation();
        location.setShows(null);
        location = locationRepository.saveAndFlush(location);
        Seat seat1 = TestData.getTestSeatWithId(null);
        seat1.setRowNr(1);
        seat1.setSeatNr(1);
        seat1.setLocation(location);
        seat1.setRealSeat(true);
        Seat seat2 = TestData.getTestSeatWithId(null);
        seat2.setRowNr(1);
        seat2.setSeatNr(2);
        seat2.setLocation(location);
        seat2.setRealSeat(false);
        Seat seat3 = TestData.getTestSeatWithId(null);
        seat3.setRowNr(1);
        seat3.setSeatNr(3);
        seat3.setLocation(location);
        seat3.setRealSeat(true);
        seat1 = seatRepository.saveAndFlush(seat1);
        seat2 = seatRepository.saveAndFlush(seat2);
        seat3 = seatRepository.saveAndFlush(seat3);
        SaveShowDto saveShowDto = new SaveShowDto();
        ShowDto showDto = TestData.getTestShowDto();
        showDto.setId(null);
        showDto.setLocation(locationMapper.locationToLocationDto(location));
        showDto.setEventId(event.getId());
        TicketDto ticketDto1 = new TicketDto();
        ticketDto1.setSeatId(seat1.getId());
        ticketDto1.setSector('A');
        ticketDto1.setPrice(100.50f);
        TicketDto ticketDto2 = new TicketDto();
        ticketDto2.setSeatId(seat3.getId());
        ticketDto2.setSector('B');
        ticketDto2.setPrice(60.50f);
        List<TicketDto> ticketDtos = new ArrayList<>();
        ticketDtos.add(ticketDto1);
        ticketDtos.add(ticketDto2);
        saveShowDto.setShow(showDto);
        saveShowDto.setTickets(ticketDtos);
        String body = objectMapper.writeValueAsString(saveShowDto);

        //Act
        MvcResult mvcResult = this.mockMvc.perform(post(SHOW_BASE_URI)
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

        ShowDto savedShow = objectMapper.readValue(response.getContentAsString(), ShowDto.class);

        softly = new SoftAssertions();
        softly.assertThat(savedShow.getDescription()).isEqualTo(showDto.getDescription());
        softly.assertThat(savedShow.getTitle()).isEqualTo(showDto.getTitle());
        softly.assertThat(savedShow.getStartTime()).isEqualTo(showDto.getStartTime());
        softly.assertThat(savedShow.getStartDate()).isEqualTo(showDto.getStartDate());
        softly.assertThat(savedShow.getLocation()).isEqualTo(locationMapper.locationToLocationDto(location));
        softly.assertThat(showRepository.findById(savedShow.getId())).isNotNull();
        softly.assertAll();

        assertThat(ticketRepository.findByShow(showRepository.findById(savedShow.getId()).get()).size()).isEqualTo(2);
    }

    @Test
    public void givenNothing_whenSaveWithInvalidProperties_then422() throws Exception {
        //Arrange
        Event event = TestData.getTestEvent();
        event.setArtists(null);
        event.setShows(null);
        event = eventRepository.saveAndFlush(event);
        Location location = TestData.getTestLocation();
        location.setShows(null);
        location = locationRepository.saveAndFlush(location);
        Seat seat1 = TestData.getTestSeatWithId(null);
        seat1.setRowNr(1);
        seat1.setSeatNr(1);
        seat1.setLocation(location);
        seat1.setRealSeat(true);
        Seat seat2 = TestData.getTestSeatWithId(null);
        seat2.setRowNr(1);
        seat2.setSeatNr(2);
        seat2.setLocation(location);
        seat2.setRealSeat(false);
        Seat seat3 = TestData.getTestSeatWithId(null);
        seat3.setRowNr(1);
        seat3.setSeatNr(3);
        seat3.setLocation(location);
        seat3.setRealSeat(true);
        seat1 = seatRepository.saveAndFlush(seat1);
        seat2 = seatRepository.saveAndFlush(seat2);
        seat3 = seatRepository.saveAndFlush(seat3);
        SaveShowDto saveShowDto = new SaveShowDto();
        ShowDto showDto = TestData.getTestShowDto();
        showDto.setId(null);
        showDto.setLocation(locationMapper.locationToLocationDto(location));
        showDto.setEventId(event.getId());
        showDto.setEndTime(showDto.getStartTime().plusMinutes(event.getDuration()));
        TicketDto ticketDto1 = new TicketDto();
        ticketDto1.setSeatId(seat1.getId());
        ticketDto1.setSector('A');
        ticketDto1.setPrice(100.50f);
        TicketDto ticketDto2 = new TicketDto();
        ticketDto2.setSeatId(seat3.getId());
        ticketDto2.setSector('A');
        ticketDto2.setPrice(60.50f);
        List<TicketDto> ticketDtos = new ArrayList<>();
        ticketDtos.add(ticketDto1);
        ticketDtos.add(ticketDto2);
        saveShowDto.setShow(showDto);
        saveShowDto.setTickets(ticketDtos);
        String body = objectMapper.writeValueAsString(saveShowDto);

        //Act
        MvcResult mvcResult = this.mockMvc.perform(post(SHOW_BASE_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .content(body)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        //Assert
        assertThat(response.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
    }

    @Test
    public void givenOneShow_whenPurchaseFreeTickets_thenNewOrder() throws Exception {
        //Arrange
        Show show = saveShow();

        Seat seat1 = TestData.getTestSeatWithId(null);
        seat1.setLocation(show.getLocation());
        Seat seat2 = TestData.getTestSeatWithId(null);
        seat2.setLocation(show.getLocation());
        seat1 = seatRepository.saveAndFlush(seat1);
        seat2 = seatRepository.saveAndFlush(seat2);
        Ticket ticket1 = TestData.getTestTicketWithId(null);
        ticket1.setSector('A');
        ticket1.setPrice(100.60f);
        ticket1.setSeat(seat1);
        ticket1.setUser(null);
        ticket1.setShow(show);
        ticket1.setStatus(Ticket.Status.FREE);
        Ticket ticket2 = TestData.getTestTicketWithId(null);
        ticket2.setSector('B');
        ticket2.setPrice(90.60f);
        ticket2.setSeat(seat2);
        ticket2.setUser(null);
        ticket2.setShow(show);
        ticket2.setStatus(Ticket.Status.FREE);
        ticketRepository.saveAndFlush(ticket1);
        ticketRepository.saveAndFlush(ticket2);
        ApplicationUser user = TestData.getTestUserWithId(null);
        usersRepository.saveAndFlush(user);
        Long id = ticketRepository.getNextOrderId();

        OrderTicketsDto order = new OrderTicketsDto();
        List<Long> seatIds = new ArrayList<>();
        seatIds.add(seat1.getId());
        seatIds.add(seat2.getId());
        order.setSeatIds(seatIds);
        order.setMode("PURCHASE");
        String body = objectMapper.writeValueAsString(order);

        //Act
        MvcResult mvcResult = this.mockMvc.perform(post(SHOW_BASE_URI + "/{id}/order", show.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(body)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        //Assert
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        softly.assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
        softly.assertAll();

        Long[] dto = objectMapper.readValue(response.getContentAsString(), Long[].class);
        Long orderId = dto[0];

        assertThat(orderId).isEqualTo(id + 1);

    }

    @Test
    public void givenOneShow_whenReserveFreeTickets_thenNewOrder() throws Exception {
        //Arrange
        Show show = saveShow();

        Seat seat1 = TestData.getTestSeatWithId(null);
        seat1.setLocation(show.getLocation());
        Seat seat2 = TestData.getTestSeatWithId(null);
        seat2.setLocation(show.getLocation());
        seat1 = seatRepository.saveAndFlush(seat1);
        seat2 = seatRepository.saveAndFlush(seat2);
        Ticket ticket1 = TestData.getTestTicketWithId(null);
        ticket1.setSector('A');
        ticket1.setPrice(100.60f);
        ticket1.setSeat(seat1);
        ticket1.setUser(null);
        ticket1.setShow(show);
        ticket1.setStatus(Ticket.Status.FREE);
        Ticket ticket2 = TestData.getTestTicketWithId(null);
        ticket2.setSector('B');
        ticket2.setPrice(90.60f);
        ticket2.setSeat(seat2);
        ticket2.setUser(null);
        ticket2.setShow(show);
        ticket2.setStatus(Ticket.Status.FREE);
        ticketRepository.saveAndFlush(ticket1);
        ticketRepository.saveAndFlush(ticket2);
        ApplicationUser user = TestData.getTestUserWithId(null);
        usersRepository.saveAndFlush(user);
        Long id = ticketRepository.getNextOrderId();

        OrderTicketsDto order = new OrderTicketsDto();
        List<Long> seatIds = new ArrayList<>();
        seatIds.add(seat1.getId());
        seatIds.add(seat2.getId());
        order.setSeatIds(seatIds);
        order.setMode("RESERVE");
        String body = objectMapper.writeValueAsString(order);

        //Act
        MvcResult mvcResult = this.mockMvc.perform(post(SHOW_BASE_URI + "/{id}/order", show.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(body)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        //Assert
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        softly.assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
        softly.assertAll();

        Long[] dto = objectMapper.readValue(response.getContentAsString(), Long[].class);
        Long orderId = dto[0];

        assertThat(orderId).isEqualTo(id + 1);

    }

    @Test
    public void givenOneShow_whenPurchaseUnavailableTickets_then422() throws Exception {
        //Arrange
        Show show = saveShow();

        Seat seat1 = TestData.getTestSeatWithId(null);
        seat1.setLocation(show.getLocation());
        Seat seat2 = TestData.getTestSeatWithId(null);
        seat2.setLocation(show.getLocation());
        seat1 = seatRepository.saveAndFlush(seat1);
        seat2 = seatRepository.saveAndFlush(seat2);
        Ticket ticket1 = TestData.getTestTicketWithId(null);
        ticket1.setSector('A');
        ticket1.setPrice(100.60f);
        ticket1.setSeat(seat1);
        ticket1.setUser(null);
        ticket1.setShow(show);
        ticket1.setStatus(Ticket.Status.PURCHASED);
        Ticket ticket2 = TestData.getTestTicketWithId(null);
        ticket2.setSector('B');
        ticket2.setPrice(90.60f);
        ticket2.setSeat(seat2);
        ticket2.setUser(null);
        ticket2.setShow(show);
        ticket2.setStatus(Ticket.Status.FREE);
        ticketRepository.saveAndFlush(ticket1);
        ticketRepository.saveAndFlush(ticket2);
        ApplicationUser user = TestData.getTestUserWithId(null);
        usersRepository.saveAndFlush(user);

        OrderTicketsDto order = new OrderTicketsDto();
        List<Long> seatIds = new ArrayList<>();
        seatIds.add(seat1.getId());
        seatIds.add(seat2.getId());
        order.setSeatIds(seatIds);
        order.setMode("PURCHASE");
        String body = objectMapper.writeValueAsString(order);

        //Act
        MvcResult mvcResult = this.mockMvc.perform(post(SHOW_BASE_URI + "/{id}/order", show.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(body)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        //Assert
        assertThat(response.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
    }

    @Test
    public void givenOneShow_whenReserveUnavailableTickets_then422() throws Exception {
        //Arrange
        Show show = saveShow();

        Seat seat1 = TestData.getTestSeatWithId(null);
        seat1.setLocation(show.getLocation());
        Seat seat2 = TestData.getTestSeatWithId(null);
        seat2.setLocation(show.getLocation());
        seat1 = seatRepository.saveAndFlush(seat1);
        seat2 = seatRepository.saveAndFlush(seat2);
        Ticket ticket1 = TestData.getTestTicketWithId(null);
        ticket1.setSector('A');
        ticket1.setPrice(100.60f);
        ticket1.setSeat(seat1);
        ticket1.setUser(null);
        ticket1.setShow(show);
        ticket1.setStatus(Ticket.Status.PURCHASED);
        Ticket ticket2 = TestData.getTestTicketWithId(null);
        ticket2.setSector('B');
        ticket2.setPrice(90.60f);
        ticket2.setSeat(seat2);
        ticket2.setUser(null);
        ticket2.setShow(show);
        ticket2.setStatus(Ticket.Status.FREE);
        ticketRepository.saveAndFlush(ticket1);
        ticketRepository.saveAndFlush(ticket2);
        ApplicationUser user = TestData.getTestUserWithId(null);
        usersRepository.saveAndFlush(user);

        OrderTicketsDto order = new OrderTicketsDto();
        List<Long> seatIds = new ArrayList<>();
        seatIds.add(seat1.getId());
        seatIds.add(seat2.getId());
        order.setSeatIds(seatIds);
        order.setMode("RESERVE");
        String body = objectMapper.writeValueAsString(order);

        //Act
        MvcResult mvcResult = this.mockMvc.perform(post(SHOW_BASE_URI + "/{id}/order", show.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(body)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        //Assert
        assertThat(response.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
    }

    @Test
    public void givenOneShow_whenFindByLocation_ThenReturnEvent() throws Exception
    {
        Show show = saveShow();

        MvcResult mvcResult = this.mockMvc.perform(get(SHOW_BASE_URI + "/location")
            .param("id", show.getLocation().getId().toString())
            .param("page", "0")
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        assert(response.getContentAsString().contains(show.getLocation().getDescription()));
    }

    @Test
    public void givenNothing_whenFindByLocation_ThenAssertNotFound() throws Exception
    {
        MvcResult mvcResult = this.mockMvc.perform(get(SHOW_BASE_URI + "/location")
            .param("id", show.getLocation().getId().toString())
            .param("page", "0")
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        assert(!response.getContentAsString().contains(show.getLocation().getDescription()));
    }

    @Test
    public void givenOneShow_whenFindAll_thenReturnShow()
        throws Exception {
        Show show = saveShow();

        MvcResult mvcResult = this.mockMvc.perform(get(SHOW_BASE_URI + "/pagination")
            .param("page", "0")
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        assert(response.getContentAsString().contains(show.getTitle()));
    }

    @Test
    public void givenNoShow_whenFindAll_thenNotFound()
        throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get(SHOW_BASE_URI + "/pagination")
            .param("page", "0")
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    @Test
    public void givenOneShow_whenSearchWithWrongCriteria_thenNotFound()
        throws Exception {
        Show show = saveShow();

        MvcResult mvcResult = this.mockMvc.perform(get(SHOW_BASE_URI + "/search")
            .param("title", "xxx")
            .param("location", show.getLocation().getDescription())
            .param("price", "0")
            .param("date", show.getStartDate().toString())
            .param("time", show.getStartTime().toString())
            .param("page", "0")
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    @Test
    @WithMockUser(DEFAULT_USER)
    public void givenOneShowWithSoldTickets_whenTicketPurchaseReport_thenRespondsWithPDF() throws Exception {
        //Arrange
        Show show = saveShow();

        ApplicationUser user = TestData.getTestUserWithId(null);
        user = usersRepository.saveAndFlush(user);
        Seat seat1 = TestData.getTestSeatWithId(null);
        seat1.setLocation(show.getLocation());
        seat1 = seatRepository.saveAndFlush(seat1);
        Ticket ticket1 = TestData.getTestTicketWithId(null);
        ticket1.setSector('A');
        ticket1.setPrice(100.60f);
        ticket1.setSeat(seat1);
        ticket1.setUser(user);
        ticket1.setShow(show);
        ticket1.setStatus(Ticket.Status.PURCHASED);
        Long orderId = ticketRepository.getNextOrderId();
        ticket1.setOrderId(orderId);
        ticket1 = ticketRepository.saveAndFlush(ticket1);

        //Act
        MvcResult mvcResult = this.mockMvc.perform(get(SHOW_BASE_URI + "/generatepdf/purchase/{orderId}", orderId)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        //Assert
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_PDF_VALUE, response.getContentType());
    }

    @Test
    @WithMockUser(DEFAULT_USER)
    public void givenOneShowWithCanceledTickets_whenTicketCancelReport_thenRespondsWithPDF() throws Exception {
        //Arrange
        Show show = saveShow();

        ApplicationUser user = TestData.getTestUserWithId(null);
        user = usersRepository.saveAndFlush(user);
        Seat seat1 = TestData.getTestSeatWithId(null);
        seat1.setLocation(show.getLocation());
        seat1 = seatRepository.saveAndFlush(seat1);
        Ticket ticket1 = TestData.getTestTicketWithId(null);
        ticket1.setSector('A');
        ticket1.setPrice(100.60f);
        ticket1.setSeat(seat1);
        ticket1.setUser(null);
        ticket1.setShow(show);
        ticket1.setStatus(Ticket.Status.FREE);
        Long orderId = ticketRepository.getNextOrderId();
        ticket1.setOrderId(orderId);
        ticket1 = ticketRepository.saveAndFlush(ticket1);
        List<Long> ids = new ArrayList<>();
        ids.add(ticket1.getId());
        String body = objectMapper.writeValueAsString(ids);

        //Act
        MvcResult mvcResult = this.mockMvc.perform(post(SHOW_BASE_URI + "/generatepdf/cancel/{orderId}/{bonusPoints}", orderId, 10L)
            .contentType(MediaType.APPLICATION_JSON)
            .content(body)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        //Assert
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_PDF_VALUE, response.getContentType());
    }

    @Test
    @WithMockUser(DEFAULT_USER)
    public void givenOneShowWithReservedTickets_whenPurchaseReservedTickets_thenTicketsArePurchased() throws Exception {
        //Arrange
        Show show = saveShow();

        ApplicationUser user = TestData.getTestUserWithId(null);
        user = usersRepository.saveAndFlush(user);
        Seat seat1 = TestData.getTestSeatWithId(null);
        seat1.setLocation(show.getLocation());
        seat1 = seatRepository.saveAndFlush(seat1);
        Ticket ticket1 = TestData.getTestTicketWithId(null);
        ticket1.setSector('A');
        ticket1.setPrice(100.60f);
        ticket1.setSeat(seat1);
        ticket1.setUser(user);
        ticket1.setShow(show);
        ticket1.setStatus(Ticket.Status.RESERVED);
        Long orderId = ticketRepository.getNextOrderId();
        ticket1.setOrderId(orderId);
        ticket1 = ticketRepository.saveAndFlush(ticket1);
        List<Long> ticketIds = new ArrayList<>();
        ticketIds.add(ticket1.getId());
        String body = objectMapper.writeValueAsString(ticketIds);
        Long lastOrderId = ticketRepository.getNextOrderId();

        //Act
        MvcResult mvcResult = this.mockMvc.perform(post(SHOW_BASE_URI + "/purchase-reserved")
            .contentType(MediaType.APPLICATION_JSON)
            .content(body)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        //Assert
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        Long newOrderId = objectMapper.readValue(response.getContentAsString(), Long.class);

        assertThat(newOrderId).isEqualTo(lastOrderId + 1);
    }

    @Test
    @WithMockUser(DEFAULT_USER)
    public void givenOneShowWithSoldTickets_whenGetAllTicketsByUserId_thenCorrectTicketDtos() throws Exception {
        //Arrange
        Show show = saveShow();

        ApplicationUser user = TestData.getTestUserWithId(null);
        user = usersRepository.saveAndFlush(user);
        Seat seat1 = TestData.getTestSeatWithId(null);
        seat1.setLocation(show.getLocation());
        seat1 = seatRepository.saveAndFlush(seat1);
        Seat seat2 = TestData.getTestSeatWithId(null);
        seat2.setLocation(show.getLocation());
        seat2 = seatRepository.saveAndFlush(seat2);
        Ticket ticket1 = TestData.getTestTicketWithId(null);
        ticket1.setSector('A');
        ticket1.setPrice(100.60f);
        ticket1.setSeat(seat1);
        ticket1.setUser(user);
        ticket1.setShow(show);
        ticket1.setStatus(Ticket.Status.PURCHASED);
        Ticket ticket2 = TestData.getTestTicketWithId(null);
        ticket2.setSector('A');
        ticket2.setPrice(100.60f);
        ticket2.setSeat(seat2);
        ticket2.setUser(user);
        ticket2.setShow(show);
        ticket2.setStatus(Ticket.Status.PURCHASED);
        Long orderId = ticketRepository.getNextOrderId();
        ticket1.setOrderId(orderId);
        ticket2.setOrderId(orderId);
        ticket1 = ticketRepository.saveAndFlush(ticket1);
        ticket2 = ticketRepository.saveAndFlush(ticket2);

        //Act
        MvcResult mvcResult = this.mockMvc.perform(get(SHOW_BASE_URI + "/my-orders")
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        //Assert
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        List<TicketDto> ticketDtos = Arrays.asList(objectMapper.readValue(response.getContentAsString(), TicketDto[].class));

        assertThat(ticketDtos.size()).isEqualTo(2);
    }

    @Test
    @WithMockUser(DEFAULT_USER)
    public void givenOneShowWithSoldTickets_whenCancelTickets_ticketsCancelledAndBonusPointsReduced() throws Exception {
        //Arrange
        Show show = saveShow();

        ApplicationUser user = TestData.getTestUserWithId(null);
        user = usersRepository.saveAndFlush(user);
        Seat seat1 = TestData.getTestSeatWithId(null);
        seat1.setLocation(show.getLocation());
        seat1 = seatRepository.saveAndFlush(seat1);
        Ticket ticket1 = TestData.getTestTicketWithId(null);
        ticket1.setSector('A');
        ticket1.setPrice(100.60f);
        ticket1.setSeat(seat1);
        ticket1.setUser(user);
        ticket1.setShow(show);
        ticket1.setStatus(Ticket.Status.PURCHASED);
        Long orderId = ticketRepository.getNextOrderId();
        ticket1.setOrderId(orderId);
        ticket1 = ticketRepository.saveAndFlush(ticket1);
        List<Long> ticketIds = new ArrayList<>();
        ticketIds.add(ticket1.getId());
        String body = objectMapper.writeValueAsString(ticketIds);

        //Act
        MvcResult mvcResult = this.mockMvc.perform(post(SHOW_BASE_URI + "/cancel-tickets")
            .contentType(MediaType.APPLICATION_JSON)
            .content(body)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        //Assert
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        Long reducedBonusPoints = objectMapper.readValue(response.getContentAsString(), Long.class);

        //TODO: assert right value for reducedBonusPoints
        assertThat(reducedBonusPoints).isEqualTo(0L);
    }

    @Test
    @WithMockUser(DEFAULT_USER)
    public void givenOneShowWithSoldTickets_whenGetPagesWithAllTicketsByUserId_thenReturnTicketsPage() throws Exception {
        //Arrange
        Show show = saveShow();
        ApplicationUser user = TestData.getTestUserWithId(null);
        user = usersRepository.saveAndFlush(user);
        Seat seat1 = TestData.getTestSeatWithId(null);
        seat1.setLocation(show.getLocation());
        seat1 = seatRepository.saveAndFlush(seat1);
        Ticket ticket1 = TestData.getTestTicketWithId(null);
        ticket1.setSector('A');
        ticket1.setPrice(100.60f);
        ticket1.setSeat(seat1);
        ticket1.setUser(user);
        ticket1.setShow(show);
        ticket1.setStatus(Ticket.Status.PURCHASED);
        Long orderId = ticketRepository.getNextOrderId();
        ticket1.setOrderId(orderId);
        ticket1 = ticketRepository.saveAndFlush(ticket1);


        //Act
        MvcResult mvcResult = this.mockMvc.perform(get(SHOW_BASE_URI + "/tickets-pagination")
            .param("page", "0")
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        //Assert
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        assert(response.getContentAsString().contains(ticket1.getId().toString()));
        assert(response.getContentAsString().contains(ticket1.getOrderId().toString()));
    }

}

