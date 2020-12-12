package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.*;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.LocationMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.ShowMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Location;
import at.ac.tuwien.sepm.groupphase.backend.entity.Show;
import at.ac.tuwien.sepm.groupphase.backend.service.LocationService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.lang.invoke.MethodHandles;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/venues")
public class LocationEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final LocationService locationService;
    private final LocationMapper locationMapper;


    @Autowired
    public LocationEndpoint(LocationService locationService, LocationMapper locationMapper) {
        this.locationService = locationService;
        this.locationMapper = locationMapper;
    }

    @GetMapping(value = "/{id}")
    @ApiOperation(value = "Get detailed information about a specific location",
        authorizations = {@Authorization(value = "apiKey")})
    public LocationDto find(@PathVariable Long id) {
        LOGGER.info("GET /api/v1/venues/{}", id);
        return locationMapper.locationToLocationDto(locationService.findOne(id));
    }

    @GetMapping
    @ApiOperation(value = "Get list of locations without details", authorizations = {@Authorization(value = "apiKey")})
    public List<LocationDto> findAll() {
        LOGGER.info("GET /api/v1/venues");
        return locationMapper.locationToLocationDto(locationService.findAll());
    }

    @GetMapping(value = "/pagination")
    @ApiOperation(value = "Get list of locations pagination", authorizations = {@Authorization(value = "apiKey")})
    public Page<LocationDto> findAll(@RequestParam(required = false) int page) {
        Sort sort = Sort.by("description");
        Pageable pageable = PageRequest.of(page, 15, sort);
        Page<Location> locationPage = locationService.findAll(page);
        List<LocationDto> locationDtos = locationMapper.locationToLocationDto(locationPage.getContent());
        LOGGER.info("GET /api/v1/venues/pagination");
        return new PageImpl<>(locationDtos, pageable, locationPage.getTotalElements());
    }

    @GetMapping(value = "/search")
    @ApiOperation(value = "Search for locations with specific params",
        authorizations = {@Authorization(value = "apiKey")})
    public Page<LocationDto> findByParam(@RequestParam(defaultValue = "%") String description,
                                         @RequestParam(defaultValue = "%") String city,
                                         @RequestParam(defaultValue = "%") String country,
                                         @RequestParam(defaultValue = "%") String street,
                                         @RequestParam(defaultValue = "%") String zipcode,
                                         @RequestParam(required = false) int page) {
        Sort sort = Sort.by("description");
        Pageable pageable = PageRequest.of(page, 15, sort);
        Page<Location> locationPage = locationService.findByParam(description, city, country, street, zipcode, page);
        List<LocationDto> locationDtos = locationMapper.locationToLocationDto(locationPage.getContent());
        LOGGER.info("GET /api/v1/venues/search");
        return new PageImpl<>(locationDtos, pageable, locationPage.getTotalElements());
    }

    @GetMapping(value = "/countries")
    @ApiOperation(value = "Get all countries",
        authorizations = {@Authorization(value = "apiKey")})
    public List<String> getAllCountries() {
        LOGGER.info("GET/api/v1/venues/countries");
        return locationService.getCountriesOrdered();
    }

    @GetMapping(value = "/cities")
    @ApiOperation(value = "Get all cities",
        authorizations = {@Authorization(value = "apiKey")})
    public List<String> getAllCities() {
        LOGGER.info("GET/api/v1/venues/cities");
        return locationService.getCitiesOrdered();
    }

    @GetMapping(value = "/zipCodes")
    @ApiOperation(value = "Get all zipCodes",
        authorizations = {@Authorization(value = "apiKey")})
    public List<String> getAllZipCodes() {
        LOGGER.info("GET/api/v1/venues/zipCodes");
        return locationService.getZipCodesOrdered();
    }

    @GetMapping(value = "/description")
    @ApiOperation(value = "Get all descriptions",
        authorizations = {@Authorization(value = "apiKey")})
    public List<String> getAllDescription() {
        LOGGER.info("GET/api/v1/venues/description");
        return locationService.getDescriptionOrdered();
    }

    @Secured("ROLE_ADMIN")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @ApiOperation(value = "Post a new Location", authorizations = {@Authorization(value = "apiKey")})
    public LocationDto saveLocation(@Valid @RequestBody LocationDto locationDto) {
        LOGGER.info("POST /api/v1/venues body: {}", locationDto);
        return locationMapper.locationToLocationDto(
            locationService.saveLocation(locationMapper.locationDtotoLocation(locationDto)));
    }

    @GetMapping(value = "/{id}/seats")
    @ApiOperation(value = "Get seats of a specific location",
        authorizations = {@Authorization(value = "apiKey")})
    public List<SeatDto> getSeats(@PathVariable Long id) {
        LOGGER.info("GET /api/v1/venues/{}/seats", id);
        return locationMapper.seatsToSeatDtos(locationService.getSeats(id));
    }
}
