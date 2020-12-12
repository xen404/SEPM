package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.TopEventDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.SoldTicketsPerEvent;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;


import java.util.HashMap;
import java.util.List;

@Mapper
public interface TopEventMapper {
    @IterableMapping(qualifiedByName = "topEvent")
    List<TopEventDto> topEventToTopEventDto2 (List<SoldTicketsPerEvent> topEventWholeInfo);

    HashMap<String, List<TopEventDto>> topEventToTopEventDto (HashMap<String, List<SoldTicketsPerEvent>> topEvent);

}
