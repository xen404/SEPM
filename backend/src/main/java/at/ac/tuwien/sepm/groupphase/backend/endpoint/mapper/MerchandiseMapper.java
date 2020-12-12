package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.MerchandiseDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ShowDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import at.ac.tuwien.sepm.groupphase.backend.entity.Merchandise;
import at.ac.tuwien.sepm.groupphase.backend.entity.Show;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper
public interface MerchandiseMapper {

    @Named("merchandiseDto")
    @Mapping(target = "event", source = "eventId")
    Merchandise merchandiseDtoToMerchandise (MerchandiseDto dto);

    @Named("merchandise")
    @Mapping(target = "eventId", source = "event")
    MerchandiseDto merchandiseToMerchandiseDto (Merchandise entity);


    default Event eventIdToEvent(Long eventId) {
        Event event = new Event();
        event.setId(eventId);
        return event;
    }

    default Long eventToEventId (Event event) {
        return event.getId();
    }
}
