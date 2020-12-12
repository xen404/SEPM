package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ArtistDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.EventDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.ArtistMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Artist;
import at.ac.tuwien.sepm.groupphase.backend.repository.ArtistRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.ArtistService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;

import java.lang.invoke.MethodHandles;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/artists")
public class ArtistEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final ArtistService artistService;
    private final ArtistMapper artistMapper;

    @Autowired
    public ArtistEndpoint(ArtistService artistService, ArtistMapper artistMapper) {
        this.artistService = artistService;
        this.artistMapper = artistMapper;
    }

    @GetMapping
    @ApiOperation(value = "Get list of artists without details", authorizations = {@Authorization(value = "apiKey")})
    public List<ArtistDto> findAll() {
        LOGGER.info("GET /api/v1/artists");
        return artistMapper.artistToArtistDto(artistService.findAllByOrderByName());
    }

    @GetMapping(value = "/pagination")
    @ApiOperation(value = "Search for artist with a parameter",
        authorizations = {@Authorization(value = "apiKey")})
    public Page<ArtistDto> findAll(@RequestParam(required = false) int page) {
        Sort sort = Sort.by("name");
        Pageable pageable = PageRequest.of(page, 15, sort);
        Page<Artist> artistPage = artistService.findAllByOrderByName(page);
        List<ArtistDto> artistDtos = artistMapper.artistToArtistDto(artistPage.getContent());
        LOGGER.info("GET /api/v1/artists/pagination");
        return new PageImpl<>(artistDtos, pageable, artistPage.getTotalElements());
    }

    @GetMapping(value = "/{id}")
    @ApiOperation(value = "Get detailed information about a specific artist",
        authorizations = {@Authorization(value = "apiKey")})
    public ArtistDto find(@PathVariable Long id) {
        LOGGER.info("GET /api/v1/artists/{}", id);
        return artistMapper.artistToSimpleArtistDto(artistService.findOne(id));
    }

    @GetMapping(value = "/search")
    @ApiOperation(value = "Search for artist with a parameter",
        authorizations = {@Authorization(value = "apiKey")})
    public Page<ArtistDto> findByName(@RequestParam(required = false) String name, @RequestParam(required = false) int page) {
        LOGGER.info("GET /api/v1/artists/search");
        Sort sort = Sort.by("name");
        Pageable pageable = PageRequest.of(page, 15, sort);
        Page<Artist> artistPage = artistService.findByNameContainingIgnoreCase(name, page);
        List<ArtistDto> artistDtos = artistMapper.artistToArtistDto(artistPage.getContent());
        return new PageImpl<>(artistDtos, pageable, artistPage.getTotalElements());
    }

}
