package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ApplicationUserDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.MerchandiseDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.MerchandiseMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Merchandise;
import at.ac.tuwien.sepm.groupphase.backend.service.MerchandiseService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/merchandise")
public class MerchandiseEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final MerchandiseService merchandiseService;
    private final MerchandiseMapper merchandiseMapper;


    @Autowired
    public MerchandiseEndpoint(MerchandiseService merchandiseService, MerchandiseMapper merchandiseMapper) {
        this.merchandiseService = merchandiseService;
        this.merchandiseMapper = merchandiseMapper;
    }

    @Secured("ROLE_ADMIN")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @ApiOperation(value = "Create new merchandise item", authorizations = {@Authorization(value = "apiKey")})
    public MerchandiseDto create(@Valid @RequestBody MerchandiseDto merchandiseDto) {
        LOGGER.info("POST /api/v1/merchandise body: {}", merchandiseDto);
        Merchandise merchandise = merchandiseMapper.merchandiseDtoToMerchandise(merchandiseDto);
        return merchandiseMapper.merchandiseToMerchandiseDto(merchandiseService.save(merchandise));
    }

    @Secured("ROLE_ADMIN")
    @GetMapping(value = "/index")
    @ApiOperation(value = "Get index for the next merchandise to be added",
                                authorizations = {@Authorization(value = "apiKey")})
    public Long getIndex() {
        LOGGER.info("GET /api/v1/merchandise/index");
        return merchandiseService.getNextIndex();
    }

    @Secured("ROLE_ADMIN")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/{id}/upload")
    @ApiOperation(value = "Upload image for merchandise with specific id",
                                    authorizations = {@Authorization(value = "apiKey")})
    public void uploadImage(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        LOGGER.info("POST /api/v1/merchandise/{}/upload", id);
        merchandiseService.uploadImage(id, file);
    }

    @GetMapping(value = "/{id}/image")
    @ApiOperation(value = "Get image for merchandise with specific id",
                                    authorizations = {@Authorization(value = "apiKey")})
    public String getImage(@PathVariable Long id) throws IOException {
        LOGGER.info("POST /api/v1/merchandise/{}/image", id);
        return merchandiseService.getImage(id);
    }


    @GetMapping(value = "/pages/{pageNumber}")
    @ApiOperation(value = "Get all merchandise items", authorizations = {@Authorization(value = "apiKey")})
    public Page<MerchandiseDto> findAll(@PathVariable final Integer pageNumber) {
        LOGGER.info("GET /api/v1/merchandise/pages/" + pageNumber);
        Pageable pageable = PageRequest.of(pageNumber, 15);
        Page<Merchandise> merchandisePage = merchandiseService.getAllMerchandiseItems(pageable);
        List<MerchandiseDto> merchandiseDtos = new ArrayList<>();

        for (Merchandise m: merchandisePage) {
            MerchandiseDto merchandiseDto = merchandiseMapper.merchandiseToMerchandiseDto(m);
            merchandiseDtos.add(merchandiseDto);
        }
        return new PageImpl<>(merchandiseDtos, pageable, merchandisePage.getTotalElements());
    }

    @GetMapping(value = "/available/pages/{pageNumber}")
    @ApiOperation(value = "Get available merchandise items", authorizations = {@Authorization(value = "apiKey")})
    public Page<MerchandiseDto> getAvailable(@RequestParam(value = "bonus") Long bonusPoints,
                                             @PathVariable final Integer pageNumber) {
        LOGGER.info("GET /api/v1/merchandise/available/pages/" + pageNumber
                            + ", less than or equal to: {}", bonusPoints);
        Pageable pageable = PageRequest.of(pageNumber, 20);
        Page<Merchandise> merchandisePage = merchandiseService.getAvailableMerchandise(bonusPoints, pageable);
        List<MerchandiseDto> merchandiseDtos = new ArrayList<>();

        for (Merchandise m: merchandisePage) {
            MerchandiseDto merchandiseDto = merchandiseMapper.merchandiseToMerchandiseDto(m);
            merchandiseDtos.add(merchandiseDto);
        }
        return new PageImpl<>(merchandiseDtos, pageable, merchandisePage.getTotalElements());
    }

    @GetMapping(value = "/event/{event_id}")
    @ApiOperation(value = "Get all merchandise items for a specific event",
                            authorizations = {@Authorization(value = "apiKey")})
    public List<MerchandiseDto> getMerchandiseForEvent(@PathVariable("event_id") Long eventId) {
        LOGGER.info("GET /api/v1/merchandise/event/{} get merchandise for event with id", eventId);
        List<Merchandise> merchandiseEntities = merchandiseService.getMerchandiseForEvent(eventId);
        List<MerchandiseDto> merchandiseDtos = new ArrayList<>();

        for (Merchandise m: merchandiseEntities) {
            MerchandiseDto merchandiseDto = merchandiseMapper.merchandiseToMerchandiseDto(m);
            merchandiseDtos.add(merchandiseDto);
        }
        return merchandiseDtos;
    }

    @GetMapping(value = "/{id}")
    @ApiOperation(value = "Get one merchandise item with id", authorizations = {@Authorization(value = "apiKey")})
    public MerchandiseDto getMerchandiseById(@PathVariable("id") Long id) {
        LOGGER.info("GET /api/v1/merchandise/{}", id);

        return merchandiseMapper.merchandiseToMerchandiseDto(merchandiseService.getMerchandiseById(id));
    }
}
