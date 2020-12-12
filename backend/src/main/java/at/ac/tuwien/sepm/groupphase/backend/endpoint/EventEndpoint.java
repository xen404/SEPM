package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.*;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.EventMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.TopEventMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import at.ac.tuwien.sepm.groupphase.backend.service.EventService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/events")
public class EventEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final EventService eventService;
    private final EventMapper eventMapper;
    private final TopEventMapper topEventMapper;

    @Autowired
    public EventEndpoint(EventService eventService, EventMapper eventMapper, TopEventMapper topEventMapper) {
        this.eventService = eventService;
        this.eventMapper = eventMapper;
        this.topEventMapper = topEventMapper;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @ApiOperation(value = "Save new event in database", authorizations = {@Authorization(value = "apiKey")})
    public EventDto saveEvent(@Valid @RequestBody EventDto eventDto) {
        LOGGER.info("POST /api/v1/events body: {}", eventDto);
        return eventMapper.entityToDto(
            eventService.saveEvent(eventMapper.dtoToEntity(eventDto)));
    }


    @GetMapping(value = "/{id}")
    @ApiOperation(value = "Get a specific event",
        authorizations = {@Authorization(value = "apiKey")})
    public EventDto find(@PathVariable Long id) {
        LOGGER.info("GET /api/v1/events/{}", id);
        return eventMapper.entityToDto(eventService.findOne(id));
    }

    @GetMapping(value = "/categories")
    @ApiOperation(value = "Get all categories", authorizations = {@Authorization(value = "apiKey")})
    public List<String> getCategories() {
        LOGGER.info("GET /api/v1/events/categories");
        return eventService.categoriesToStringList();
    }

    @GetMapping(value = "/top")
    @ApiOperation(value = "Get top events for each category", authorizations = {@Authorization(value = "apiKey")})
    public HashMap<String, List<TopEventDto>> getTopEvents(@Param("month") int month) {
        LOGGER.info("GET /api/v1/events/top");
        return topEventMapper.topEventToTopEventDto(eventService.getTopEvents(month));
    }


    @GetMapping(value = "/category/{id}")
    @ApiOperation(value = "Get list of simple events with category = id",
        authorizations = {@Authorization(value = "apiKey")})
    public List<SimpleEventDto> findAllByCategory(@PathVariable Long id) {
        LOGGER.info("GET /api/v1/events/category/{}", id);
        return eventMapper.eventToSimpleEventDto(eventService.findAllByCategory(id));
    }

    @GetMapping(value = "/{id}/simple")
    @ApiOperation(value = "Get a specific (simple) event",
        authorizations = {@Authorization(value = "apiKey")})
    public SimpleEventDto findSimple(@PathVariable Long id) {
        LOGGER.info("GET /api/v1/events/{}/simple", id);
        return eventMapper.eventToSimpleEventDto(eventService.findOne(id));
    }

    @GetMapping(value = "/simple")
    @ApiOperation(value = "Get all (simple) events",
        authorizations = {@Authorization(value = "apiKey")})
    public List<SimpleEventDto> findAllSimple() {
        LOGGER.info("GET /api/v1/events/simple");
        return eventMapper.eventToSimpleEventDto(eventService.findAll());
    }

    @GetMapping(value = "/{id}/image")
    @ApiOperation(value = "Get image for event with specific id", authorizations = {@Authorization(value = "apiKey")})
    public String getImage(@PathVariable Long id) throws IOException {
        LOGGER.info("GET /api/v1/events/{}/image", id);
        return eventService.getImage(id);
    }

    @GetMapping(value = "/size")
    @ApiOperation(value = "Get last event added", authorizations = {@Authorization(value = "apiKey")})
    public int getLastImage() {
        LOGGER.info("GET /api/v1/last");
        return eventService.getLastEvent();
    }

    @Secured("ROLE_ADMIN")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/{id}/upload")
    @ApiOperation(value = "Upload image for event with specific id",
        authorizations = {@Authorization(value = "apiKey")})
    public void uploadImage(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        LOGGER.info("POST /api/v1/news/{}/upload", id);
        eventService.uploadImage(id, file);
    }

    @GetMapping(value = "/pagination")
    @ApiOperation(value = "Search for event with a parameter",
        authorizations = {@Authorization(value = "apiKey")})
    public Page<EventDto> findAll(@RequestParam(required = false) int page) {
        Sort sort = Sort.by("title");
        Pageable pageable = PageRequest.of(page, 15, sort);
        Page<Event> eventPage = eventService.findAllByOrderByTitle(page);
        List<EventDto> eventDtos = eventMapper.eventToEventDto(eventPage.getContent());
        LOGGER.info("GET /api/v1/events/pagination");
        return new PageImpl<>(eventDtos, pageable, eventPage.getTotalElements());
    }

    @GetMapping(value = "/search")
    @ApiOperation(value = "Search for events with specific params",
        authorizations = {@Authorization(value = "apiKey")})
    public Page<EventDto> findByParam(@RequestParam(defaultValue = "%") String title,
                                      @RequestParam(required = false) Long categoryId,
                                      @RequestParam(required = false) int duration,
                                      @RequestParam(required = false) int page) {
        Sort sort = Sort.by("title");
        Pageable pageable = PageRequest.of(page, 15, sort);
        Page<Event> eventPage = eventService.findByParam(title, categoryId, duration, page);
        List<EventDto> eventDtos = eventMapper.eventToEventDto(eventPage.getContent());
        LOGGER.info("GET /api/v1/events/search");
        return new PageImpl<>(eventDtos, pageable, eventPage.getTotalElements());
    }


    @GetMapping(value = "/artist")
    @ApiOperation(value = "Get events of the artist", authorizations = {@Authorization(value = "apiKey")})
    public Page<EventDto> findByArtist(@RequestParam(required = false) Long id,
                                        @RequestParam(required = false) int page) {
        Sort sort = Sort.by("title");
        Pageable pageable = PageRequest.of(page, 15, sort);
        Page<Event> eventPage = eventService.findEventsByArtist(id, pageable);
        List<EventDto> eventDto = eventMapper.eventToEventDto(eventPage.getContent());
        LOGGER.info("GET /api/v1/events/artist/{}", id);
        return new PageImpl<>(eventDto, pageable, eventPage.getTotalElements());
    }

}
