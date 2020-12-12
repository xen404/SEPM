package at.ac.tuwien.sepm.groupphase.backend.entity;

import com.sun.istack.Nullable;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "show")
public class Show {

    @GeneratedValue
    @Id
    private Long id;

    private String title;

    @ManyToOne
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    // @Column(nullable = false)
    private LocalTime startTime;

    //@Column(nullable = false)
    private LocalTime endTime;

    private LocalDate startDate;

    private LocalDate endDate;

    private String description;

    private int duration;

    @ManyToOne()
    //@OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    public Show() {

    }

    public Show(Long id, String title) {
        this.id = id;
        this.title = title;
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

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
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

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Show show = (Show) o;
        return duration == show.duration
            && Objects.equals(id, show.id)
            && Objects.equals(title, show.title)
            && Objects.equals(location, show.location)
            && Objects.equals(startTime, show.startTime)
            && Objects.equals(endTime, show.endTime)
            && Objects.equals(startDate, show.startDate)
            && Objects.equals(endDate, show.endDate)
            && Objects.equals(description, show.description)
            && Objects.equals(event, show.event);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, location, startTime, endTime, startDate, endDate, description, duration, event);
    }

    @Override
    public String toString() {
        return "Show{"
            + "id=" + id
            + ", title='" + title + '\''
            + ", locationId=" + location.getId()
            + ", startTime=" + startTime
            + ", endTime=" + endTime
            + ", startDate=" + startDate
            + ", endDate=" + endDate
            + ", description='" + description + '\''
            + ", duration=" + duration
            + ", event with id=" + event.getId()
            + '}';
    }
}
