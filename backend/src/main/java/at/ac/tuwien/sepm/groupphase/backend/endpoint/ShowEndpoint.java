package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.*;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.ShowMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.TicketMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Show;
import at.ac.tuwien.sepm.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepm.groupphase.backend.service.ShowService;
import at.ac.tuwien.sepm.groupphase.backend.service.TicketService;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.lang.invoke.MethodHandles;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/shows")
public class ShowEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final ShowService showService;
    private final TicketService ticketService;
    private final ShowMapper showMapper;
    private final TicketMapper ticketMapper;
    private final UserService userService;

    @Autowired
    public ShowEndpoint(ShowService showService, ShowMapper showMapper, TicketService ticketService,
                        TicketMapper ticketMapper, UserService userService) {
        this.showService = showService;
        this.showMapper = showMapper;
        this.ticketService = ticketService;
        this.userService = userService;
        this.ticketMapper = ticketMapper;
    }

    @GetMapping(value = "/{id}/seats")
    @ApiOperation(value = "Get free, reserved and purchased seats of a specific show",
        authorizations = {@Authorization(value = "apiKey")})
    public List<ShowSeatDto> getSeats(@PathVariable Long id) {
        LOGGER.info("GET /api/v1/shows/{}/seats", id);
        return showMapper.seatTicketMapToSeatShowDtos(showService.getSeatsWithTicket(id));
    }

    @GetMapping(value = "/{id}")
    @ApiOperation(value = "Get detailed information about a specific show",
        authorizations = {@Authorization(value = "apiKey")})
    public ShowDto find(@PathVariable Long id) {
        LOGGER.info("GET /api/v1/shows/{}", id);
        return showMapper.showToShowDto(showService.findOne(id));
    }

    private Pageable createPageRequest(int pageNumber) {
        return PageRequest.of(pageNumber, 30, Sort.by("id").descending());
    }

    @GetMapping(value = "/{id}/prices")
    @ApiOperation(value = "Get prices of a specific show",
        authorizations = {@Authorization(value = "apiKey")})
    public List<PriceDto> getPrices(@PathVariable Long id) {
        LOGGER.info("GET /api/v1/shows/{}/prices", id);
        return showMapper.ticketsToPriceDtos(showService.getTicketsOfShow(id));
    }

    @GetMapping(path = "/generatepdf/purchase/{orderId}")
    @ApiOperation(value = "Generate PDF for purchased tickets",
        authorizations = {@Authorization(value = "apiKey")})
    public ResponseEntity<InputStreamResource> ticketPurchaseReport(@PathVariable Long orderId,
                                                                    Principal principal) throws Exception {

        LOGGER.info("GET /api/v1/shows/generatepdf/purchase/{}", orderId);

        return showService.ticketPurchaseReport(orderId, principal.getName());

    }

    @PostMapping(path = "/generatepdf/cancel/{orderId}/{bonusPoints}")
    @ApiOperation(value = "Generate pdf for canceled ticket",
        authorizations = {@Authorization(value = "apiKey")})
    public ResponseEntity<InputStreamResource> ticketCancelReport(@PathVariable Long orderId,
                                                                  @PathVariable Long bonusPoints,
                                                                  @Valid @RequestBody List<Long> ids,
                                                                  Principal principal) throws Exception {

        LOGGER.info("POST /api/v1/shows/generatepdf/cancel/{}/{}", orderId, bonusPoints);

        return showService.ticketCancelReport(orderId, ids, principal.getName(), bonusPoints);
    }

    @GetMapping(value = "/pagination")
    @ApiOperation(value = "Search for show with a parameter",
        authorizations = {@Authorization(value = "apiKey")})
    public Page<ShowDto> findAll(@RequestParam(required = false) int page) {
        Sort sort = Sort.by("title");
        Pageable pageable = PageRequest.of(page, 15, sort);
        Page<Show> showPage = showService.findAllByOrderByTitle(page);
        List<ShowDto> showDtos = showMapper.showsToShowDtos(showPage.getContent());
        LOGGER.info("GET /api/v1/shows/pagination");
        return new PageImpl<>(showDtos, pageable, showPage.getTotalElements());
    }


    @Secured("ROLE_ADMIN")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @ApiOperation(value = "Post a new Show", authorizations = {@Authorization(value = "apiKey")})
    public ShowDto saveShow(@Valid @RequestBody SaveShowDto saveShowDto) {
        LOGGER.info("POST /api/v1/shows body: {}", saveShowDto);
        return showMapper.showToShowDto(showService.saveShow(showMapper.saveShowDtoToSaveShow(saveShowDto)));
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/{id}/order")
    @ApiOperation(value = "Order tickets", authorizations = {@Authorization(value = "apiKey")})
    public Long[] orderTickets(@PathVariable Long id, @Valid @RequestBody OrderTicketsDto orderTicketsDto,
                               Principal principal) {
        LOGGER.info("POST /api/v1/shows/{}/order body: {}", id, orderTicketsDto);
        return showService.orderTickets(orderTicketsDto.getSeatIds(), id,
            principal.getName(),
            orderTicketsDto.getMode());

    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/cancel-tickets")
    @ApiOperation(value = "Cancel tickets", authorizations = {@Authorization(value = "apiKey")})
    public long cancelTickets(@Valid @RequestBody List<Long> ids, Principal principal) {
        LOGGER.info("POST /api/v1/shows/cancel-tickets");

        return showService.cancelTickets(ids, principal.getName());
    }

    @PostMapping(value = "/purchase-reserved")
    @ApiOperation(value = "Purchase previously reserved tickets", authorizations = {@Authorization(value = "apiKey")})
    public Long purchaseReservedTickets(@Valid @RequestBody List<Long> ticketIds, Principal principal) {
        LOGGER.info("POST /api/v1/shows/purchase-reserved body: {}", ticketIds);
        return showService.purchaseReservedTickets(ticketIds, principal.getName());
    }


    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/my-orders")
    @ApiOperation(value = "Get all tickets of the user", authorizations = {@Authorization(value = "apiKey")})
    public List<TicketDto> getAllTicketsByUserId(Principal principal) {
        Long id = userService.findUserByEmail(principal.getName()).getId();
        LOGGER.info("GET /api/v1/shows/my-orders/{}", id);
        return ticketMapper.ticketToTicketDto(showService.getAllTicketsByUserId(id));
    }


    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/tickets-pagination")
    @ApiOperation(value = "Get all tickets of the user", authorizations = {@Authorization(value = "apiKey")})
    public Page<TicketDto> getPagesWithAllTicketsByUserId(@RequestParam(required = false) int page,
                                                          Principal principal) {
        Long id = userService.findUserByEmail(principal.getName()).getId();
        Sort sort = Sort.by("order_id");
        Pageable pageable = PageRequest.of(page, 25, sort);
        Page<Ticket> ticketPage = showService.getPageWithAllTicketsByUserId(id, page);
        List<TicketDto> ticketDtos = ticketMapper.ticketToTicketDto(ticketPage.getContent());
        LOGGER.info("GET /api/v1/shows/my-orders/{}", id);
        return new PageImpl<>(ticketDtos, pageable, ticketPage.getTotalElements());
    }

    @GetMapping(value = "/search")
    @ApiOperation(value = "Search for events with specific params",
        authorizations = {@Authorization(value = "apiKey")})
    public Page<ShowDto> findByParam(@RequestParam(defaultValue = "%") String title,
                                     @RequestParam(defaultValue = "%") String location,
                                     @RequestParam(defaultValue = "%") int price,
                                     @RequestParam(defaultValue = "%") String date,
                                     @RequestParam(defaultValue = "%") String time,
                                     @RequestParam(required = false) int page) {
        Sort sort = Sort.by("title");
        Pageable pageable = PageRequest.of(page, 15, sort);
        Page<Show> showPage = showService.findByParams(title, location, price, date, time, page);
        List<ShowDto> showDtos = showMapper.showsToShowDtos(showPage.getContent());
        LOGGER.info("GET /api/v1/shows/search");
        return new PageImpl<>(showDtos, pageable, showPage.getTotalElements());
    }


    @GetMapping(value = "/location")
    @ApiOperation(value = "Get shows for a location", authorizations = {@Authorization(value = "apiKey")})
    public Page<ShowDto> findByLocation(@RequestParam(value = "id", required = false) Long id,
                                        @RequestParam(value = "page", required = false) int page) {
        LOGGER.info("GET /api/v1/shows/location/{}", id);
        Sort sort = Sort.by("title");
        Pageable pageable = PageRequest.of(page, 15, sort);
        Page<Show> showPage = showService.findShowsByLocation(id, pageable);
        List<ShowDto> showDto = showMapper.showsToShowDtos(showPage.getContent());
        return new PageImpl<>(showDto, pageable, showPage.getTotalElements());
    }
}
