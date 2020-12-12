package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.*;
import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

@Mapper
public interface ShowMapper {

    default List<ShowSeatDto> seatTicketMapToSeatShowDtos(Map<Seat, Ticket> map) {
        if (map == null) {
            return null;
        }

        List<ShowSeatDto> showSeatDtos = new ArrayList<>();
        map.forEach((key, value) -> showSeatDtos.add(new ShowSeatDto(
            key.getId(),
            value != null ? value.getSector() : '-',
            key.getRowNr(),
            key.getSeatNr(),
            value != null ? value.getStatus().name() : "NOSEAT",
            value != null ? value.getPrice() : 0)));
        return showSeatDtos;
    }


    @Mapping(target = "event", source = "eventId")
    Show showDtoToShow(ShowDto showDto);

    default Event eventIdToEvent(Long eventId) {
        Event event = new Event();
        event.setId(eventId);
        return event;
    }

    default SaveShow saveShowDtoToSaveShow(SaveShowDto saveShowDto) {
        if (saveShowDto == null) {
            return null;
        }

        SaveShow saveShow = new SaveShow();
        Show show = showDtoToShow(saveShowDto.getShow());
        saveShow.setShow(show);

        List<Ticket> tickets = new ArrayList<>();
        for (TicketDto ticketDto : saveShowDto.getTickets()) {
            Ticket ticket = new Ticket();
            ticket.setShow(show);
            Seat seat = new Seat();
            seat.setId(ticketDto.getSeatId());
            ticket.setSeat(seat);
            ticket.setStatus(Ticket.Status.FREE);
            ticket.setSector(ticketDto.getSector());
            ticket.setPrice(ticketDto.getPrice());
            tickets.add(ticket);
        }
        saveShow.setTickets(tickets);
        return saveShow;
    }

    @Named("Show")
    @Mapping(target = "eventId", source = "event.id")
    @Mapping(target = "eventTitle", source = "event.title")
    ShowDto showToShowDto(Show show);

    @IterableMapping(qualifiedByName = "Show")
    List<ShowDto> showsToShowDtos(List<Show> shows);

    default List<PriceDto> ticketsToPriceDtos(List<Ticket> tickets) {
        if (tickets == null) {
            return null;
        }

        List<PriceDto> priceDtos = new ArrayList<>();
        tickets.stream().filter(distinctByKey(Ticket::getSector)).forEach(
            (item) -> {
                PriceDto priceDto = new PriceDto(item.getSector(), item.getPrice());
                priceDtos.add(priceDto); });
        return priceDtos;
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    default List<Long> orderTicketsDtoToSeatIds(OrderTicketsDto orderTicketsDto) {
        if (orderTicketsDto == null) {
            return null;
        }

        return orderTicketsDto.getSeatIds();
    }



    @Named("Ticket")
    TicketDto ticketToTicketDto(Ticket ticket);

    @IterableMapping(qualifiedByName = "Ticket")
    List<TicketDto> ticketsToTicketDtos(List<Ticket> tickets);

    @IterableMapping(qualifiedByName = "Ticket")
    List<Ticket> ticketsDtotoTickets(List<TicketDto> ticketDtos);

    @Mapping(target = "shows", ignore = true)
    EventDto eventToEventDto(Event event);

    @Mapping(target = "shows", ignore = true)
    Event eventDtoToEvent(EventDto eventDto);
}

