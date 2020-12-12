package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.DetailedNewsDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.NewsInquiryDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SimpleNewsDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.NewsMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.News;
import at.ac.tuwien.sepm.groupphase.backend.service.NewsService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.validation.Valid;
import java.lang.invoke.MethodHandles;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/news")
public class NewsEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final NewsService newsService;
    private final NewsMapper newsMapper;

    @Autowired
    public NewsEndpoint(NewsService newsService, NewsMapper newsMapper) {
        this.newsService = newsService;
        this.newsMapper = newsMapper;
    }


    @GetMapping(value = "/{id}")
    @ApiOperation(value = "Get detailed information about a specific news",
        authorizations = {@Authorization(value = "apiKey")})
    public DetailedNewsDto find(@PathVariable Long id) {
        LOGGER.info("GET /api/v1/news/{}", id);
        return newsMapper.newsToDetailedNewsDto(newsService.findOne(id));
    }

    @GetMapping(value = "/seen/{id}")
    @ApiOperation(value = "Get seen news with specific id",
        authorizations = {@Authorization(value = "apiKey")})
    public DetailedNewsDto setSeenNewsHome(@PathVariable Long id, Principal principal) {
        LOGGER.info("GET /api/v1/news/seen/{}", id);
        return newsMapper.newsToDetailedNewsDto(newsService.setSeenNews(id, principal.getName()));
    }

    @GetMapping(value = "/homeunseen")
    @ApiOperation(value = "Get unseen news for a user", authorizations = {@Authorization(value = "apiKey")})
    public List<SimpleNewsDto> findLastUnseenNews(Principal principal) {
        LOGGER.info("GET /api/v1/news/homeunseen");

        return newsMapper.newsToSimpleNewsDto(newsService.findLastUnseenNews(principal.getName()));
    }

    @GetMapping(value = "/size")
    @ApiOperation(value = "Get last news added", authorizations = {@Authorization(value = "apiKey")})
    public int getLastImage() {
        LOGGER.info("GET /api/v1/last");
        return newsService.getLastNews();
    }


    @GetMapping(value = "/unseen/pages/{pageNumber}")
    @ApiOperation(value = "Get seen news for a user", authorizations = {@Authorization(value = "apiKey")})
    public Page<SimpleNewsDto> findUnseenNews(Principal principal, @PathVariable final Integer pageNumber) {
        LOGGER.info("GET /api/v1/news/unseen");

        Pageable pageable = PageRequest.of(pageNumber, 8);
        Page<News> newsPage = newsService.findUnseenNews(principal.getName(), pageable);
        List<SimpleNewsDto> newsDtos = new ArrayList<>();

        for (News m : newsPage) {
            SimpleNewsDto newsDto = newsMapper.newsToSimpleNewsDto(m);
            newsDtos.add(newsDto);
        }
        return new PageImpl<>(newsDtos, pageable, newsPage.getTotalElements());
    }

    @GetMapping(value = "/seen/pages/{pageNumber}")
    @ApiOperation(value = "Get seen news for a user", authorizations = {@Authorization(value = "apiKey")})
    public Page<SimpleNewsDto> findSeenNews(Principal principal, @PathVariable final Integer pageNumber) {
        LOGGER.info("GET /api/v1/news/seen");

        Pageable pageable = PageRequest.of(pageNumber, 8);
        Page<News> newsPage = newsService.findSeenNews(principal.getName(), pageable);
        List<SimpleNewsDto> newsDtos = new ArrayList<>();

        for (News m : newsPage) {
            SimpleNewsDto newsDto = newsMapper.newsToSimpleNewsDto(m);
            newsDtos.add(newsDto);
        }
        return new PageImpl<>(newsDtos, pageable, newsPage.getTotalElements());

    }

    @GetMapping(value = "/{id}/image")
    @ApiOperation(value = "Get image for news with specific id", authorizations = {@Authorization(value = "apiKey")})
    public String getImage(@PathVariable Long id) {
        LOGGER.info("GET /api/v1/{}/image", id);
        return newsService.getImage(id);
    }

    @Secured("ROLE_ADMIN")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/{id}/upload")
    @ApiOperation(value = "Upload image for news with specific id", authorizations = {@Authorization(value = "apiKey")})
    public void uploadImage(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        LOGGER.info("POST /api/v1/news/{}/upload", id);
        newsService.uploadImage(id, file);
    }

    @Secured("ROLE_ADMIN")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @ApiOperation(value = "Publish a new news", authorizations = {@Authorization(value = "apiKey")})
    public DetailedNewsDto create(@Valid @RequestBody NewsInquiryDto newsDto) {
        LOGGER.info("POST /api/v1/news body: {}", newsDto);
        return newsMapper.newsToDetailedNewsDto(
            newsService.publishNews(newsMapper.newsInquiryDtoToNews(newsDto)));
    }
}
