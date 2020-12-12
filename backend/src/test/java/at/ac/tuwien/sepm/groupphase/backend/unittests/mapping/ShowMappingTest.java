package at.ac.tuwien.sepm.groupphase.backend.unittests.mapping;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ShowSeatDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ShowDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.ShowMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.ShowMapperImpl;
import at.ac.tuwien.sepm.groupphase.backend.entity.Seat;
import at.ac.tuwien.sepm.groupphase.backend.entity.Show;
import at.ac.tuwien.sepm.groupphase.backend.entity.Ticket;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ShowMapperImpl.class)
@ActiveProfiles("test")
public class ShowMappingTest implements TestData {

    @Autowired
    private ShowMapper showMapper;

    @Test
    public void givenNothing_whenShowToShowDto_thenDtoHasAllProperties() {
        //Arrange
        Show show = TestData.getTestShow();

        //Act
        ShowDto showDto = showMapper.showToShowDto(show);

        //Assert
        SoftAssertions softly = new SoftAssertions();
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
    public void givenNothing_whenSeatTicketMapToSeatShowDtos_thenDtoHasAllProperties() {
        //Arrange
        Map<Seat, Ticket> map = new HashMap<>();
        //Seats
        Seat seat1 = TestData.getTestSeatWithId(1L);
        seat1.setRowNr(1);
        seat1.setSeatNr(5);
        Seat seat2 = TestData.getTestSeatWithId(2L);
        seat2.setRowNr(2);
        seat2.setSeatNr(6);
        Seat seat3 = TestData.getTestSeatWithId(3L);
        seat3.setRowNr(3);
        seat3.setSeatNr(7);
        seat3.setRealSeat(false);
        //Tickets
        Ticket ticket1 = TestData.getTestTicketWithId(1L);
        ticket1.setSector('A');
        ticket1.setPrice(50.60f);
        ticket1.setStatus(Ticket.Status.FREE);
        ticket1.setSeat(seat1);
        Ticket ticket2 = TestData.getTestTicketWithId(2L);
        ticket2.setSector('B');
        ticket2.setPrice(20.40f);
        ticket2.setStatus(Ticket.Status.PURCHASED);
        ticket2.setSeat(seat2);
        map.put(seat1, ticket1);
        map.put(seat2, ticket2);
        map.put(seat3, null);
        List<ShowSeatDto> testDataList = new ArrayList<>();
        testDataList.add(new ShowSeatDto(seat1.getId(), ticket1.getSector(), seat1.getRowNr(), seat1.getSeatNr(), "FREE", ticket1.getPrice()));
        testDataList.add(new ShowSeatDto(seat2.getId(), ticket2.getSector(), seat2.getRowNr(), seat2.getSeatNr(), "PURCHASED", ticket2.getPrice()));
        testDataList.add(new ShowSeatDto(seat3.getId(), '-', seat3.getRowNr(), seat3.getSeatNr(), "NOSEAT", 0));

        //Act
        List<ShowSeatDto> showSeatDtos = showMapper.seatTicketMapToSeatShowDtos(map);

        //Assert
        assertThat(showSeatDtos.size()).isEqualTo(3);
        assertThat(showSeatDtos).containsOnlyElementsOf(testDataList);
    }

    @Test
    public void givenNothing_whenShowDtoToShow_thenShowWithCorrectProperties() {
        //Arrange
        Show testShow = TestData.getTestShow();
        testShow.getEvent().setId(2L);
        ShowDto showDto = showMapper.showToShowDto(testShow);

        //Act
        Show show = showMapper.showDtoToShow(showDto);

        //Assert
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(show.getId()).isEqualTo(showDto.getId());
        softly.assertThat(show.getTitle()).isEqualTo(showDto.getTitle());
        softly.assertThat(show.getDescription()).isEqualTo(showDto.getDescription());
        softly.assertThat(show.getStartTime()).isEqualTo(showDto.getStartTime());
        softly.assertThat(show.getEndTime()).isEqualTo(showDto.getEndTime());
        softly.assertThat(show.getEvent().getId()).isEqualTo(showDto.getEventId());
        softly.assertAll();
    }
}
