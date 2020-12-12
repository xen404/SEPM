package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ArtistDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.EventDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Artist;
import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

@Mapper
public interface ArtistMapper {

    @Named("Artist")
    @Mapping(target = "eventIds", source = "events")
    ArtistDto artistToSimpleArtistDto(Artist artist);

    default List<Long> eventsToEventIds(List<Event> events) {
        if (events == null) {
            return null;
        }
        List<Long> eventIds = new ArrayList<>();
        events.forEach(
            (item) -> {
                eventIds.add(item.getId());
            }
        );
        return eventIds;
    }

    //This is necessary since the SimpleMessageDto misses the text property and the collection mapper can't handle
    //missing fields.
    @IterableMapping(qualifiedByName = "Artist")
    List<ArtistDto> artistToArtistDto(List<Artist> artist);

    ArtistDto artistToArtistDto(Artist artist);

    @Mapping(target = "artists", ignore = true)
    EventDto eventToEventDto(Event event);

    @IterableMapping(qualifiedByName = "Event")
    List<EventDto> eventsToEventDto(List<Event> events);

    Artist artistDtoToArtist(ArtistDto artistDto);
}

