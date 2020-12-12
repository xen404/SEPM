package at.ac.tuwien.sepm.groupphase.backend.entity;

import net.bytebuddy.dynamic.loading.InjectionClassLoader;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import util.EventCategory;

import javax.persistence.*;
import java.sql.Time;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "event")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private EventCategory category;

    @Column(length = 1024)
    private String description;
    private int duration;

    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime startTime;
    private LocalTime endTime;

    private String image;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(
        mappedBy = "event",
        cascade = CascadeType.REMOVE,
        orphanRemoval = true
    )
    private List<Show> shows;

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany
    @JoinTable(
        name = "Event_Artist_Jtable",
        joinColumns = @JoinColumn(name = "event_id"),
        inverseJoinColumns = @JoinColumn(name = "artist_id"))
    List<Artist> artists;


    public Event() {

    }

    public Event(Long id, String title,
                 EventCategory category,
                 String description,
                 int duration,
                 LocalDate startDate,
                 LocalDate endDate,
                 LocalTime startTime,
                 LocalTime endTime,
                 String image,
                 List<Show> shows,
                 List<Artist> artists) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.description = description;
        this.duration = duration;
        this.startDate = startDate;
        this.endDate = endDate;
        this.image = image;
        this.shows = shows;
        this.artists = artists;
        this.endTime = endTime;
        this.startTime = startTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public EventCategory getCategory() {
        return category;
    }

    public void setCategory(EventCategory category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<Show> getShows() {
        return shows;
    }

    public void setShows(List<Show> shows) {
        this.shows = shows;
    }

    public List<Artist> getArtists() {
        return artists;
    }

    public void setArtists(List<Artist> artists) {
        this.artists = artists;
    }

    public static final class EventBuilder {
        private Long id;
        private String title;
        private EventCategory category;
        private String description;
        private int duration;
        private LocalDate startDate;
        private LocalDate endDate;
        private LocalTime startTime;
        private LocalTime endTime;

        private String image;

        private List<Show> shows;

        private List<Artist> artists;

        private EventBuilder() {
        }


        public static EventBuilder anEvent() {
            return new EventBuilder();
        }

        public EventBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public EventBuilder withTitle(String title) {
            this.title = title;
            return this;
        }

        public EventBuilder withCategory(EventCategory category) {
            this.category = category;
            return this;
        }

        public EventBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public EventBuilder withDuration(int duration) {
            this.duration = duration;
            return this;
        }

        public EventBuilder withStartDate(LocalDate startDate) {
            this.startDate = startDate;
            return this;
        }

        public EventBuilder withEndDate(LocalDate endDate) {
            this.endDate = endDate;
            return this;
        }

        public EventBuilder withStartTime(LocalTime startTime) {
            this.startTime = startTime;
            return this;
        }

        public EventBuilder withEndTime(LocalTime endTime) {
            this.endTime = endTime;
            return this;
        }

        public EventBuilder withImage(String image) {
            this.image = image;
            return this;
        }


        public EventBuilder withShows(List<Show> shows) {
            this.shows = shows;
            return this;
        }

        public EventBuilder withArtists(List<Artist> artists) {
            this.artists = artists;
            return this;
        }

        public Event build() {
            Event event = new Event();
            event.setId(id);
            event.setTitle(title);
            event.setCategory(category);
            event.setDescription(description);
            event.setDuration(duration);
            event.setImage(image);
            event.setShows(shows);
            event.setArtists(artists);
            event.setStartDate(startDate);
            event.setEndDate(endDate);
            //event.setNews(null);
            return event;
        }

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Event event = (Event) o;
        return duration == event.duration
            && Objects.equals(id, event.id)
            && Objects.equals(title, event.title)
            && category == event.category
            && Objects.equals(description, event.description)
            && Objects.equals(startDate, event.startDate)
            && Objects.equals(endDate, event.endDate)
            && Objects.equals(startTime, event.startTime)
            && Objects.equals(endTime, event.endTime)
            && Objects.equals(image, event.image)
            && Objects.equals(shows, event.shows)
            && Objects.equals(artists, event.artists);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, category, description, duration, startDate, endDate, startTime, endTime, image, shows, artists);
    }

    @Override
    public String toString() {
        return "Event{"
            + "id=" + id
            + ", title='" + title + '\''
            + ", category=" + category
            + ", description='" + description + '\''
            + ", duration=" + duration
            + ", startDate=" + startDate
            + ", endDate=" + endDate
            + ", startTime=" + startTime
            + ", endTime=" + endTime
            + ", image='" + image + '\''
            + ", shows=" + shows
            + ", artists=" + artists
            + '}';
    }
}

