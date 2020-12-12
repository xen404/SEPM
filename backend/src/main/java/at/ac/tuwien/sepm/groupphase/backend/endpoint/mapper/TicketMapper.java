package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ShowDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SimpleEventDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.TicketDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import at.ac.tuwien.sepm.groupphase.backend.entity.Show;
import at.ac.tuwien.sepm.groupphase.backend.entity.Ticket;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper
public interface TicketMapper {

    @Named("ticket")
    @Mapping(target = "showTitle", source = "show.title")
    @Mapping(target = "showId", source = "show.id")
    @Mapping(target = "showStartDate", source = "show.startDate")
    @Mapping(target = "showStartTime", source = "show.startTime")
    TicketDto ticketToTicketDto(Ticket ticket);

    Ticket ticketDtoToTicket(TicketDto ticketDto);

    @IterableMapping(qualifiedByName = "ticket")
    List<TicketDto> ticketToTicketDto(List<Ticket> tickets);

}
