package at.ac.tuwien.sepm.groupphase.backend.basetest;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.*;
import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import at.ac.tuwien.sepm.groupphase.backend.repository.TopEventRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.EventService;
import com.github.javafaker.App;
import org.springframework.security.crypto.password.PasswordEncoder;
import util.EventCategory;

import javax.annotation.PostConstruct;
import javax.persistence.Id;
import java.io.File;
import java.sql.Time;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

public interface TestData {

    String BASE_URI = "/api/v1";

    Long ID = 1L;
    String TEST_NEWS_TITLE = "Title";
    String TEST_NEWS_SUMMARY = "Summary";
    String TEST_NEWS_TEXT = "TestMessageText";

    LocalDateTime TEST_NEWS_PUBLISHED_AT =
        LocalDateTime.of(2019, 11, 13, 12, 15, 0, 0);


    String NEWS_BASE_URI = BASE_URI + "/news";

    String ADMIN_USER = "anna.anders@email.com";
    List<String> ADMIN_ROLES = new ArrayList<>() {
        {
            add("ROLE_ADMIN");
            add("ROLE_USER");
        }
    };
    String DEFAULT_USER = "max.muster@email.com";
    List<String> USER_ROLES = new ArrayList<>() {
        {
            add("ROLE_USER");
        }
    };

    String DEFAULT_USER_NAME = "John";
    String DEFAULT_USER_SURNAME = "Travolta";
    String DEFAULT_PASSWORD = "password";

    String TEST_SHOW_TITLE = "TestShowTitle";
    String TEST_SHOW_DESCRIPTION = "This is a super cool show!";
    String SHOW_BASE_URI = BASE_URI + "/shows";

    String TEST_LOCATION_DESCRIPTION = "Stadthalle - Halle B";
    String TEST_LOCATION_STREET = "Street";
    String TEST_LOCATION_CITY = "City";
    String TEST_LOCATION_COUNTRY = "Country";
    String TEST_LOCATION_ZIPCODE = "102";

    int TEST_ROW_NR = 2;
    int TEST_SEAT_NR = 7;
    char TEST_SECTOR = 'D';

    float TEST_PRICE = 74.50f;

    //     ****************
    //     ***NEWS_TEST***
    //     ****************

    static News getTestNews() {
        return getTestNewsWithId(ID);
    }

    static News getTestNewsWithId(long id) {

        News news = new News();
        news.setId(id);
        news.setTitle(TEST_NEWS_TITLE);
        news.setSummary(TEST_NEWS_SUMMARY);
        news.setPublishedAt(TEST_NEWS_PUBLISHED_AT);
        news.setText(TEST_NEWS_TEXT);
        Set<ApplicationUser> au = new HashSet<>();
        au.add(getTestUser());
        news.setUsers(au);

        return news;
    }



    //     ****************
    //     ***SHOW_TEST, TICKET_TEST***
    //     ****************

    static Show getTestShow() {
        return getTestShowWithId(ID);
    }

    static Show getTestShowWithId(Long id) {
        Show show = new Show();
        show.setId(id);
        show.setTitle(TEST_SHOW_TITLE);
        show.setLocation(getTestLocation());
        show.setStartDate(LocalDateTime.now().plusYears(1).toLocalDate());
        show.setStartTime(LocalTime.parse("15:00"));
        show.setEndDate(LocalDateTime.now().plusYears(1).toLocalDate());
        show.setEndTime(LocalTime.parse("19:00"));
        show.setDescription(TEST_SHOW_DESCRIPTION);
        show.setEvent(getTestEvent());
        return show;
    }

    static ShowDto getTestShowDto() { return getTestShowDtoWithId(ID);}

    static ShowDto getTestShowDtoWithId(Long id) {
        ShowDto showDto = new ShowDto();
        showDto.setId(id);
        showDto.setTitle("Magic for Humans");
        showDto.setDescription("David Copperfield World Tour");
        showDto.setLocation(getTestLocationDto());
        showDto.setStartDate(LocalDateTime.now().toLocalDate());
        showDto.setStartTime(LocalTime.parse("15:00"));
        showDto.setEndDate(LocalDateTime.now().toLocalDate());
        showDto.setEndTime(LocalTime.parse("19:00"));
        showDto.setEventId(ID);
        return showDto;
    }


    static Seat getTestSeat() {
        return getTestSeatWithId(ID);
    }

    static Seat getTestSeatWithId(Long id) {
        Seat seat = new Seat();
        seat.setId(id);
        seat.setLocation(getTestLocation());
        seat.setRowNr(TEST_ROW_NR);
        seat.setSeatNr(TEST_SEAT_NR);
        seat.setRealSeat(true);
        return seat;
    }

    static Ticket getTestTicket() {
        return getTestTicketWithId(ID);
    }

    static Ticket getTestTicketWithId(Long id) {
        Ticket ticket = new Ticket();
        ticket.setId(id);
        ticket.setDateOfPurchase(LocalDate.now());
        ticket.setSeat(getTestSeat());
        ticket.setStatus(Ticket.Status.PURCHASED);
        ticket.setShow(getTestShow());
        ticket.setUser(null);
        ticket.setSector(TEST_SECTOR);
        ticket.setPrice(70.50f);
        return ticket;
    }

    static ApplicationUser getTestUser() {
        return getTestUserWithId(ID);
    }

    static ApplicationUser getTestUserWithId(Long id) {
        ApplicationUser user = new ApplicationUser();
        user.setId(id);
        user.setEmail(DEFAULT_USER);
        user.setPassword(DEFAULT_PASSWORD);
        user.setFirstName(DEFAULT_USER_NAME);
        user.setSurname(DEFAULT_USER_SURNAME);
        user.setAccess(true);
        user.setAdmin(false);
        user.setBonusPoints(100L);
        return user;
    }


    //     ****************
    //     ***EVENT_TEST***
    //     ****************

    String EVENT_BASE_URI = BASE_URI + "/events";
    String TEST_EVENT_TITLE = "CoolEventTitle";
    String TEST_EVENT_DESCRIPTION = "La la beep beep booms bams this is Event description beep peep";
    EventCategory TEST_EVENT_CATEGORY = EventCategory.CONCERT;
    int TEST_EVENT_DURATION = 120;
    LocalDate TEST_EVENT_START_DATE = LocalDate.now().minusMonths(3L);
    LocalDate TEST_EVENT_END_DATE = LocalDate.now().minusMonths(1L);
    String TEST_EVENT_IMAGE = "ImageButItIsString";


    List<Show> TEST_EVENT_SHOWS = new ArrayList<>() {
        {
            add(getTestShowWithId(1L));
           // add(getTestShowWithId(2L));
        }
    };

    List<Artist> TEST_EVENT_ARTISTS = new ArrayList<>() {
        {
            add(getTestArtist());
        }
    };


    static Event getTestEvent() {
        return getTestEventWithId(ID);
    }

    static Event getTestEventWithId(Long id) {
        return Event.EventBuilder.anEvent()
            .withId(id)
            .withTitle(TEST_EVENT_TITLE)
            .withCategory(TEST_EVENT_CATEGORY)
            .withDescription(TEST_EVENT_DESCRIPTION)
            .withDuration(TEST_EVENT_DURATION)
            .withImage(TEST_EVENT_IMAGE)
            .withShows(TEST_EVENT_SHOWS)
            .withArtists(TEST_EVENT_ARTISTS)
            .withEndDate(TEST_EVENT_END_DATE)
            .withStartDate(TEST_EVENT_START_DATE)
            .build();
    }

    //     ****************
    //     ***ARTIST_TEST***
    //     ****************


    String ARTIST_BASE_URI = BASE_URI + "/artists";
    String TEST_ARTIST_NAME = "MrPickles";
    List<Event> TEST_ARTIST_EVENTS = new ArrayList<>(){
        {
            add(getTestEvent());
        }
    };


    static Artist getTestArtist() {
        return getTestArtistWithId(ID);
    }

    static Artist getTestArtistWithId(Long id) {
        return Artist.ArtistBuilder.anArtist()
            .withId(id)
            .withName(TEST_ARTIST_NAME)
            .withEvents(TEST_ARTIST_EVENTS)
            .build();
    }


    static List<Artist> getNRandomArtists(int n) {
        List<Artist> artist = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            artist.add(getRandomArtist());
        }
        return artist;
    }

    static Artist getRandomArtist() {
        long i = (long) (Math.random() * 999);
        return Artist.ArtistBuilder.anArtist()
            .withId(i)
            .withName(TEST_ARTIST_NAME + " " + i)
            .build();
    }

    //     **************************
    //     ***APPLICATIONUSER_TEST***
    //     **************************

    String USER_BASE_URI = BASE_URI + "/users";
    String TEST_USER_EMAIL = "test@email.com";
    String TEST_USER_FIRSTNAME = "tester";
    String TEST_USER_SURNAME = "testing";
    String TEST_USER_PASSWORD = "password";
    boolean TEST_USER_ADMIN = false;
    boolean TEST_USER_ACCESS = true;
    Long TEST_USER_BONUSPOINTS = 50L;

    static ApplicationUser getTestUser(PasswordEncoder encoder) {
        return getTestUserWithId(ID, encoder);
    }

    static ApplicationUser getTestUserWithId(Long id, PasswordEncoder encoder) {
        return ApplicationUser.ApplicationUserBuilder.anApplicationUser()
            .withId(id)
            .withFirstName(TEST_USER_FIRSTNAME)
            .withSurname(TEST_USER_SURNAME)
            .withEmail(TEST_USER_EMAIL)
            .withPassword(encoder.encode(TEST_USER_PASSWORD))
            .withAdmin(TEST_USER_ADMIN)
            .withAccess(TEST_USER_ACCESS)
            .withLoginCount(0)
            .withBonusPoint(TEST_USER_BONUSPOINTS)
            .build();
    }

    static ApplicationUserDto getTestUserDto() {
        return getTestUserDtoWithId(ID);
    }

    static ApplicationUserDto getTestUserDtoWithId(Long id) {
        ApplicationUserDto userDto = new ApplicationUserDto();
        userDto.setId(id);
        userDto.setEmail(DEFAULT_USER);
        userDto.setPassword(DEFAULT_PASSWORD);
        userDto.setFirstName(DEFAULT_USER_NAME);
        userDto.setSurname(DEFAULT_USER_SURNAME);
        userDto.setBonusPoints(TEST_USER_BONUSPOINTS);
        userDto.setAccess(true);
        userDto.setAdmin(false);
        return userDto;
    }

    static List<ApplicationUser> getNRandomUsers(int n) {
        List<ApplicationUser> users = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            users.add(getRandomUser());
        }
        return users;
    }

    static ApplicationUser getRandomUser() {
        long i = (long) (Math.random() * 999);
        return ApplicationUser.ApplicationUserBuilder.anApplicationUser()
            .withId(i)
            .withFirstName(TEST_USER_FIRSTNAME+i)
            .withSurname(TEST_USER_SURNAME+i)
            .withEmail(TEST_USER_EMAIL+i)
            .withPassword(TEST_USER_PASSWORD+i)
            .withAdmin(TEST_USER_ADMIN)
            .withAccess(false)
            .withBonusPoint(TEST_USER_BONUSPOINTS)
            .build();
    }

    //     ***************************
    //     *****  LOCATION_TEST  *****
    //     ***************************

    String LOCATION_BASE_URI = BASE_URI + "/venues";


    static Location getTestLocation() {
        return getTestLocationWithId(ID);
    }

    static Location getTestLocationWithId(Long id) {
        Location location = new Location();
        location.setId(id);
        location.setDescription(TEST_LOCATION_DESCRIPTION);
        location.setCity(TEST_LOCATION_CITY);
        location.setCountry(TEST_LOCATION_COUNTRY);
        location.setStreet(TEST_LOCATION_STREET);
        location.setZipCode(TEST_LOCATION_ZIPCODE);
        location.setShows(TEST_EVENT_SHOWS);
        return location;
    }

    static LocationDto getTestLocationDto() { return getTestLocationDtoWithId(ID); }

    static LocationDto getTestLocationDtoWithId(Long id) {
        LocationDto locationDto = new LocationDto();
        locationDto.setId(id);
        locationDto.setCity(TEST_LOCATION_CITY);
        locationDto.setCountry(TEST_LOCATION_COUNTRY);
        locationDto.setDescription(TEST_LOCATION_DESCRIPTION);
        locationDto.setStreet(TEST_LOCATION_STREET);
        locationDto.setZipCode(TEST_LOCATION_ZIPCODE);
        return locationDto;
    }


    //     **************************
    //     ***EDITEDUSER_TEST***
    //     **************************

    String TEST_EDITEDUSER_EMAIL = "edittest@email.com";
    String TEST_EDITEDUSER_FIRSTNAME = "edittester";
    String TEST_EDITEDUSER_SURNAME = "edittesting";
    String TEST_EDITEDUSER_PASSWORD = "newpassword";
    boolean TEST_EDITEDUSER_ADMIN = false;
    boolean TEST_EDITEDUSER_ACCESS = true;
    Long TEST_EDITESUSER_BONUSPOINTS = 50L;

    static EditedUserDto getTestEditedUserDto() {
        return getTestEditedUserDtoWithId(ID);
    }

    static EditedUserDto getTestEditedUserDtoWithId(Long id) {
        EditedUserDto editedUserDto = new EditedUserDto();
        editedUserDto.setFirstName(TEST_EDITEDUSER_FIRSTNAME);
        editedUserDto.setSurname(TEST_EDITEDUSER_SURNAME);
        editedUserDto.setEmail(TEST_EDITEDUSER_EMAIL);
        editedUserDto.setAccess(TEST_EDITEDUSER_ACCESS);
        editedUserDto.setAdmin(TEST_EDITEDUSER_ADMIN);
        editedUserDto.setPassword(TEST_EDITEDUSER_PASSWORD);
        editedUserDto.setId(id);
        editedUserDto.setBonusPoints(TEST_EDITESUSER_BONUSPOINTS);
        return editedUserDto;
    }

    static EditedUser getTestEditedUser() {
        return getTestEditedUserWithId(ID);
    }

    static EditedUser getTestEditedUserWithId(Long id){
        EditedUser editedUser = new EditedUser();
        editedUser.setId(id);
        editedUser.setFirstName(TEST_EDITEDUSER_FIRSTNAME);
        editedUser.setSurname(TEST_EDITEDUSER_SURNAME);
        editedUser.setEmail(TEST_EDITEDUSER_EMAIL);
        editedUser.setAccess(TEST_EDITEDUSER_ACCESS);
        editedUser.setAdmin(TEST_EDITEDUSER_ADMIN);
        editedUser.setPassword(TEST_EDITEDUSER_PASSWORD);
        editedUser.setBonusPoints(TEST_EDITESUSER_BONUSPOINTS);
        return editedUser;
    }

    //     **************************
    //     ***TOPEVENTS_TEST***
    //     **************************

    int NUMBER_OF_EVENTS_TO_GENERATE = 13;
    static List<String> getCategories() {
        List<String> categories = new ArrayList<String>();
        categories.add("Theatre");
        categories.add("Concert");
        categories.add("Opera");
        return categories;
    }

    private void generateTopEvent() {
        for (String c: getCategories()) {
            for (int i = 0; i < NUMBER_OF_EVENTS_TO_GENERATE; i++) {
                long soldTickets = new Random().nextLong();
                soldTickets = soldTickets < 0 ? soldTickets * (-1) : soldTickets;
                SoldTicketsPerEvent soldTicketsPerEvent = SoldTicketsPerEvent.SoldTicketBuilder.aSoldTicket()
                    .withTitle(TEST_EVENT_TITLE + " " + c + " " +i)
                    .withCategory(c)
                    .withAccumulatedSoldTickets(soldTickets)
                    .build();
            }
        }
    }

    //     ***************************
    //     ****  MERCHANDISE_TEST  ***
    //     ***************************

    String MERCHANDISE_BASE_URI = BASE_URI + "/merchandise";

    String TEST_MERCHANDISE_TITLE = "Cool event hoodie";
    Double TEST_MERCHANDISE_PRICE = 45.12;
    Long TEST_MERCHANDISE_BONUSPOINTS = 50L;
    String TEST_MERCHANDISE_IMAGE = File.separator + "src/main/resources/images/" + "Tshirt" + ".jpg";
    String TEST_MERCHANDISE_DESCRIPTION = "Super soft hoodie! Really nice!";


    static Merchandise getMerchandise() {
        return getMerchandiseWithId(ID);
    }

    //can either be a bonus item or not
    static Merchandise getMerchandiseWithId(Long id) {
        Merchandise merchandise = new Merchandise();
        merchandise.setId(id);
        merchandise.setTitle(TEST_MERCHANDISE_TITLE);
        merchandise.setBonusPoints(TEST_MERCHANDISE_BONUSPOINTS);
        merchandise.setPrice(TEST_MERCHANDISE_PRICE);
        merchandise.setBonus(Math.random() < 0.5);
        merchandise.setEvent(getTestEvent());
        merchandise.setDescription(TEST_MERCHANDISE_DESCRIPTION);
        //merchandise.setEvent(null);
        merchandise.setImagePath(TEST_MERCHANDISE_IMAGE);
        return merchandise;
    }

    static MerchandiseDto getMerchandiseDto() {
        MerchandiseDto merchandiseDto = new MerchandiseDto();
        merchandiseDto.setId(ID);
        merchandiseDto.setTitle(TEST_MERCHANDISE_TITLE);
        merchandiseDto.setBonusPoints(TEST_MERCHANDISE_BONUSPOINTS);
        merchandiseDto.setPrice(TEST_MERCHANDISE_PRICE);
        merchandiseDto.setBonus(Math.random() < 0.5);
        merchandiseDto.setEventId(getTestEvent().getId());
        merchandiseDto.setImagePath(TEST_MERCHANDISE_IMAGE);
        return merchandiseDto;
    }

    //bonus items and not bonus items mixed
    static List<Merchandise> getRandomMerchandise(int n) {
        List<Merchandise> merchItems = new ArrayList<>();
        for (int i = 1; i <= n; i++) {
            Merchandise merchandise = new Merchandise();
            long id = (long) (Math.random() * 999);
            long bonusPoints = (long) (Math.random() * 300);
            merchandise.setId(id);
            merchandise.setBonusPoints(bonusPoints);
            merchandise.setEvent(getTestEvent());
            merchandise.setTitle(TEST_MERCHANDISE_TITLE + " " + i);
            double price = Math.random()*70;
            merchandise.setPrice(price);
            merchandise.setBonus(Math.random() < 0.5);
            //merchandise.setEvent(getTestEvent());
            merchandise.setEvent(null);
            merchandise.setImagePath(TEST_MERCHANDISE_IMAGE);

            merchItems.add(merchandise);
        }

        return merchItems;
    }

    static Merchandise getBonusItemWithId(Long id) {
        Merchandise merchandise = new Merchandise();
        merchandise.setId(id);
        merchandise.setTitle(TEST_MERCHANDISE_TITLE);
        merchandise.setBonusPoints(TEST_MERCHANDISE_BONUSPOINTS);
        merchandise.setPrice(TEST_MERCHANDISE_PRICE);
        merchandise.setBonus(true);
        //merchandise.setEvent(getTestEvent());
        merchandise.setEvent(null);
        merchandise.setImagePath(TEST_MERCHANDISE_IMAGE);
        return merchandise;
    }

    //get n items of bonuses with  bonus points less  or equal to l
    static List<Merchandise> getRandomBonusItem(int n, Long l) {
        List<Merchandise> merchItems = new ArrayList<>();
        for (int i = 1; i <= n; i++) {
            Merchandise merchandise = new Merchandise();
            long id = (long) (Math.random() * 999);
            long bonusPoints = (long) (Math.random() * l);
            merchandise.setId(id);
            merchandise.setBonusPoints(bonusPoints);
            merchandise.setEvent(getTestEvent());
            merchandise.setTitle(TEST_MERCHANDISE_TITLE + " " + i);
            double price = Math.random()*70;
            merchandise.setPrice(price);
            merchandise.setBonus(true);
            //merchandise.setEvent(getTestEvent());
            merchandise.setEvent(null);
            merchandise.setImagePath(TEST_MERCHANDISE_IMAGE);

            merchItems.add(merchandise);
        }

        return merchItems;
    }

}
