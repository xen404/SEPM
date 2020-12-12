package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.LocationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SeatDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Location;
import at.ac.tuwien.sepm.groupphase.backend.entity.Seat;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.util.List;

@Mapper
public interface LocationMapper {

    @Named("Location")
    LocationDto locationToSimpleLocationDto(Location location);

    /*
     * This is necessary since the SimpleMessageDto misses the text property and the collection mapper can't handle
     * missing fields
     */
    @IterableMapping(qualifiedByName = "Location")
    List<LocationDto> locationToLocationDto(List<Location> location);

    LocationDto locationToLocationDto(Location location);

    Location locationDtotoLocation(LocationDto locationDto);

    @Named("Seat")
    SeatDto seatToSeatDto(Seat seat);

    @IterableMapping(qualifiedByName = "Seat")
    List<SeatDto> seatsToSeatDtos(List<Seat> seats);

}

