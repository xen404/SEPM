package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.EventDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SimpleEventDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SimpleNewsDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import at.ac.tuwien.sepm.groupphase.backend.entity.News;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper
public interface EventMapper {

    @Named("simpleEvent")
    @Mapping(target = "artists", source = "artists")
    SimpleEventDto eventToSimpleEventDto(Event event);

    /*
     * This is necessary since the SimpleMessageDto misses the text property and the collection mapper can't handle
     * missing fields.
     */
    @IterableMapping(qualifiedByName = "simpleEvent")
    List<SimpleEventDto> eventToSimpleEventDto(List<Event> events);

    @IterableMapping(qualifiedByName = "simpleEvent")
    List<EventDto> eventToEventDto(List<Event> events);

    Event dtoToEntity(EventDto eventDto);

    EventDto entityToDto(Event event);
}
