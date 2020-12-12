package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.EventRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.TopEventRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.EventService;
import org.apache.tomcat.util.codec.binary.Base64;
import org.aspectj.weaver.ast.Not;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import util.EventCategory;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.time.temporal.TemporalAdjusters.firstDayOfMonth;
import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;

@Service
public class SimpleEventService implements EventService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final EventRepository eventRepository;
    private final TopEventRepository topEventRepository;
    private final String uploadDir = Paths.get("").toAbsolutePath().toString();


    public SimpleEventService(EventRepository eventRepository, TopEventRepository topEventRepository) {
        this.eventRepository = eventRepository;
        this.topEventRepository = topEventRepository;
    }

    @Override
    public Event saveEvent(Event event) {
        if (!event.getCategory().getClass().isEnum()) {
            LOGGER.warn("Wrong input. Possible values for category: THEATRE, CONCERT, MOVIE, FESTIVAL, EXHIBITION, CIRCUS. Current category = {}", event.getCategory().toString());
            throw new ValidationException("Wrong input. Possible values for category: THEATRE, CONCERT, MOVIE, FESTIVAL, EXHIBITION, CIRCUS");
        }
        if (event.getDuration() <= 0) {
            LOGGER.warn("The duration of the event must be a positive value. Currently duration = {}", event.getDuration());
            throw new ValidationException("The duration of the event must be a positive value.");
        }
        return eventRepository.save(event);
    }

    @Override
    public Event findOne(Long id) {
        LOGGER.debug("Find message with id {}", id);
        Optional<Event> event = eventRepository.findById(id);
        if (event.isPresent()) {
            if (!event.get().getShows().isEmpty()) {
                List<Show> shows = event.get().getShows();

                ShowComparatorByStart showComparator = new ShowComparatorByStart();
                Collections.sort(shows, showComparator);

                event.get().setShows(shows);
                Event eventReturn = event.get();
                eventReturn = setStartAndEndDates(eventReturn);
                eventRepository.save(eventReturn);
                return eventReturn;
            } else {
                return event.get();
            }
        } else {
            LOGGER.warn("Could not find message with id {}", id);
            throw new NotFoundException(String.format("Could not find message with id %s", id));
        }
    }

    @Override
    public List<Event> findAll() {
        LOGGER.debug("Find all events");

        List<Event> events = eventRepository.findAll();

        if (!events.isEmpty()) {

            for (int i = 0; i < events.size(); i++) {
                if (!events.get(i).getShows().isEmpty()) {
                    List<Show> shows = events.get(i).getShows();
                    ShowComparatorByStart showComparator = new ShowComparatorByStart();
                    Collections.sort(shows, showComparator);
                    events.get(i).setShows(shows);
                    setStartAndEndDates(events.get(i));
                }
            }
        }


        return events;
    }

    public List<String> categoriesToStringList() {
        return Stream.of(TopEvent.EventCategories.values()).map(TopEvent.EventCategories::name)
            .collect(Collectors.toList());
    }

    @Override
    public List<Event> findAllByCategory(Long id) {
        LOGGER.debug("Find all events with category = ");
        List<Long> idList = eventRepository.findAllByCategory(id);

        if (idList != null && !idList.isEmpty()) {
            List<Event> events = eventRepository.findAllById(idList);

            if (!events.isEmpty()) {

                for (int i = 0; i < events.size(); i++) {
                    if (!events.get(i).getShows().isEmpty()) {
                        List<Show> shows = events.get(i).getShows();
                        ShowComparatorByStart showComparator = new ShowComparatorByStart();
                        Collections.sort(shows, showComparator);
                        events.get(i).setShows(shows);
                        setStartAndEndDates(events.get(i));
                    }
                }
            }


            return events;
        }
        return null;
    }

    @Override
    public HashMap<String, List<SoldTicketsPerEvent>> getTopEvents(int month) {

        if (month < 1 || month > 12) {
            LOGGER.warn("Month must be between 1 and 12. Currently month = {}", month);
            throw new IllegalArgumentException("Month must be between 1 and 12.");
        }

        HashMap<String, List<SoldTicketsPerEvent>> topEventsPerCategory = new HashMap<>();

        LocalDate initial = LocalDate.of(2020, month, 1);
        LocalDate start = initial.with(firstDayOfMonth());
        LocalDate end = initial.with(lastDayOfMonth());

        Date startDate = Date.valueOf(start);
        Date endDate = Date.valueOf(end);

        for (EventCategory eventCategory : EventCategory.values()) {
            topEventsPerCategory.put(eventCategory.toString(),
                topEventRepository.findTopTenByCategory(eventCategory.ordinal(), startDate, endDate));
        }

        return topEventsPerCategory;
    }

    @Override
    public String getImage(Long id) throws IOException {
        Optional<Event> event = eventRepository.findById(id);


        if (event.isPresent()) {
            if (event.get().getImage() != null) {
                Path path = Paths.get(uploadDir + event.get().getImage());

                try {
                    byte[] data = Files.readAllBytes(path);
                    String image = Base64.encodeBase64String(data);

                    if (image.charAt(0) == '/') {
                        image = "data:image/jpeg;base64," + image;
                    } else if (image.charAt(0) == 'i') {
                        image = "data:image/png;base64," + image;
                    }


                    return image;

                } catch (IOException e) {
                    LOGGER.warn("Error while getting the image!");
                }

            }
        }
        return null;

    }

    @Override
    public int getLastEvent() {
        LOGGER.debug("Get last added news");
        List<Event> au = eventRepository.findAll();
        return au.size();
    }

    //TODO: exception proper cathc block
    @Override
    public void uploadImage(Long id, MultipartFile file) {

        Optional<Event> event = eventRepository.findById(id);

        if (event.isPresent()) {
            Path pathDb = Paths.get(File.separator + "src/main/resources/images" + File.separator
                + UUID.randomUUID() + "_" + StringUtils.cleanPath(file.getOriginalFilename()));
            Path path = Paths.get(uploadDir + pathDb);
            try {
                event.get().setImage(pathDb.toString());
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                LOGGER.warn("Error while getting the image!");
            }
            eventRepository.save(event.get());
        }


    }


    public Event setStartAndEndDates(Event event) {
        List<Show> shows = event.getShows();
        event.setStartDate(shows.get(0).getStartDate());
        event.setStartTime(shows.get(0).getStartTime());
        ShowComparatorByEnd showComparatorByEnd = new ShowComparatorByEnd();
        Collections.sort(shows, showComparatorByEnd);
        event.setEndDate(shows.get(shows.size() - 1).getEndDate());
        event.setEndTime(shows.get(shows.size() - 1).getEndTime());
        return event;
    }

    @Override
    public Page<Event> findAllByOrderByTitle(int page) {
        LOGGER.debug("Find all events with pagination");
        Sort sort = Sort.by("title");
        Pageable pageable = PageRequest.of(page, 15, sort);
        Page<Event> events = eventRepository.findAllByOrderByTitle(pageable);
        if (!events.isEmpty()) {
            return events;
        } else {
            throw new NotFoundException("No events were found.");
        }
    }

    @Override
    public Page<Event> findByParam(String title, Long category, int duration, int page) {
        LOGGER.debug("Event findByParam({})", title);
        Sort sort = Sort.by("title");
        Pageable pageable = PageRequest.of(page, 15, sort);
        Page<Event> events = eventRepository.findByParam(title, category, duration, pageable);
        if (!events.isEmpty()) {
            return events;
        } else {
            throw new NotFoundException("No events fitting search criteria were found.");
        }
    }


    public static class ShowComparatorByStart implements Comparator<Show> {
        @Override
        public int compare(Show s1, Show s2) {
            int value1 = s1.getStartDate().compareTo(s2.getStartDate());
            if (value1 == 0) {
                return s1.getStartTime().compareTo(s2.getStartTime());
            } else {
                return value1;
            }
        }
    }

    public static class ShowComparatorByEnd implements Comparator<Show> {
        @Override
        public int compare(Show s1, Show s2) {
            int value1 = s1.getEndDate().compareTo(s2.getEndDate());
            if (value1 == 0) {
                return s1.getEndTime().compareTo(s2.getEndTime());
            } else {
                return value1;
            }
        }
    }

    public Page<Event> findEventsByArtist(Long id, Pageable pageable) {
        LOGGER.debug("findAllEventsForCurrentArtist({})", id);
        List<Long> eventIds = eventRepository.findEvent_IDByArtist_ID(id);
        Page<Event> events = eventRepository.findAllByIdIn(eventIds, pageable);
        if (!events.isEmpty()) {
            return events;
        } else {
            throw new NotFoundException("No events were found.");
        }
    }
}

