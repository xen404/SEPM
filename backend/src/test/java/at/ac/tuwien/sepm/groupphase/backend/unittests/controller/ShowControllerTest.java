package at.ac.tuwien.sepm.groupphase.backend.unittests.controller;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.ShowEndpoint;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.PriceDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ShowSeatDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ShowDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.ShowMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.TicketMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Seat;
import at.ac.tuwien.sepm.groupphase.backend.entity.Show;
import at.ac.tuwien.sepm.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepm.groupphase.backend.service.ShowService;
import at.ac.tuwien.sepm.groupphase.backend.service.TicketService;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
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

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = ShowEndpoint.class)
@ActiveProfiles("testSecurityOff")
@ComponentScan(basePackages = "at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper")
public class ShowControllerTest implements TestData {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ShowMapper showMapper;

    @Autowired
    private TicketMapper ticketMapper;

    @MockBean
    private ShowService showService;

    @MockBean
    private TicketService ticketService;

    @MockBean
    private UserService userService;

    @Test
    @WithMockUser
    public void givenOneShow_whenGetSeats_thenListOfSeatDtosWithCorrectProperties() throws Exception {
        //Arrange
        Map<Seat, Ticket> map = new HashMap<>();
        Seat seat1 = TestData.getTestSeatWithId(1L);
        Seat seat2 = TestData.getTestSeatWithId(2L);
        seat2.setRealSeat(false);
        Ticket ticket = TestData.getTestTicket();
        ticket.setStatus(Ticket.Status.PURCHASED);
        ticket.setSeat(seat1);
        map.put(seat1, ticket);
        map.put(seat2, null);
        when(showService.getSeatsWithTicket(anyLong())).thenReturn(map);
        List<ShowSeatDto> testDataList = new ArrayList<>();
        testDataList.add(new ShowSeatDto(seat1.getId(), ticket.getSector(), seat1.getRowNr(), seat1.getSeatNr(), "PURCHASED", ticket.getPrice()));
        testDataList.add(new ShowSeatDto(seat2.getId(), '-', seat2.getRowNr(), seat2.getSeatNr(), "NOSEAT", 0));

        //Act
        MvcResult mvcResult = mockMvc.perform(get(SHOW_BASE_URI + "/{id}/seats", ID))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        //Assert
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        softly.assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
        softly.assertAll();

        List<ShowSeatDto> showSeatDtos = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
            ShowSeatDto[].class));

        softly = new SoftAssertions();
        softly.assertThat(showSeatDtos.size()).isEqualTo(2);
        softly.assertThat(showSeatDtos).containsOnlyElementsOf(testDataList);
        softly.assertAll();
    }

    @Test
    @WithMockUser
    public void givenOneShow_whenFind_thenShowDtoHasCorrectProperties() throws Exception {
        //Arrange
        Show show = TestData.getTestShow();
        when(showService.findOne(show.getId())).thenReturn(show);

        //Act
        MvcResult mvcResult = mockMvc.perform(get(SHOW_BASE_URI + "/{id}", show.getId()))
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
    @WithMockUser
    public void givenOneShow_whenGetPrices_thenListOfPriceDtosWithCorrectProperties() throws Exception {
        //Arrange
        Show show = TestData.getTestShow();
        Ticket ticket1 = TestData.getTestTicketWithId(1L);
        ticket1.setSector('0');
        ticket1.setPrice(100.60f);
        Ticket ticket2 = TestData.getTestTicketWithId(1L);
        ticket2.setSector('0');
        ticket2.setPrice(100.60f);
        Ticket ticket3 = TestData.getTestTicketWithId(1L);
        ticket3.setSector('A');
        ticket3.setPrice(89.90f);
        Ticket ticket4 = TestData.getTestTicketWithId(1L);
        ticket4.setSector('B');
        ticket4.setPrice(50.80f);
        List<Ticket> tickets = new ArrayList<>();
        tickets.add(ticket1);
        tickets.add(ticket2);
        tickets.add(ticket3);
        tickets.add(ticket4);
        List<PriceDto> testDataList = new ArrayList<>();
        testDataList.add(new PriceDto(ticket1.getSector(), ticket2.getPrice()));
        testDataList.add(new PriceDto(ticket3.getSector(), ticket3.getPrice()));
        testDataList.add(new PriceDto(ticket4.getSector(), ticket4.getPrice()));
        when(showService.getTicketsOfShow(show.getId())).thenReturn(tickets);

        //Act
        MvcResult mvcResult = mockMvc.perform(get(SHOW_BASE_URI + "/{id}/prices", show.getId()))
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

}
