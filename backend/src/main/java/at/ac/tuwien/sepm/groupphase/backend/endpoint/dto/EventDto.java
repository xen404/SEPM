package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import util.EventCategory;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.sql.Time;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EventDto {

    private Long id;

    @NotBlank(message = "Title must not be null")
    @Size(max = 100, message = "Title must not be longer than 100 chars")
    private String title;

    @NotNull(message = "Category must not be null")
    private EventCategory category;

    @NotBlank(message = "Description must not be null")
    @Size(max = 10000, message = "Description must not be longer than 10000 chars")
    private String description;

    private int duration;

    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime startTime;
    private LocalTime endTime;

    private String image;

    private List<ShowDto> shows;

    private List<ArtistDto> artists;


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

    public List<ShowDto> getShows() {
        return shows;
    }

    public void setShows(List<ShowDto> shows) {
        this.shows = shows;
    }

    public List<ArtistDto> getArtists() {
        return artists;
    }

    public void setArtists(List<ArtistDto> artists) {
        this.artists = artists;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EventDto eventDto = (EventDto) o;
        return duration == eventDto.duration &&
            Objects.equals(id, eventDto.id) &&
            Objects.equals(title, eventDto.title) &&
            category == eventDto.category &&
            Objects.equals(description, eventDto.description) &&
            Objects.equals(startDate, eventDto.startDate) &&
            Objects.equals(endDate, eventDto.endDate) &&
            Objects.equals(startTime, eventDto.startTime) &&
            Objects.equals(endTime, eventDto.endTime) &&
            Objects.equals(image, eventDto.image) &&
            Objects.equals(shows, eventDto.shows) &&
            Objects.equals(artists, eventDto.artists);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, category, description, duration, startDate, endDate, startTime, endTime, image, shows, artists);
    }

    @Override
    public String toString() {
        return "EventDto{" +
            "id=" + id +
            ", title='" + title + '\'' +
            ", category=" + category +
            ", description='" + description + '\'' +
            ", duration=" + duration +
            ", startDate=" + startDate +
            ", endDate=" + endDate +
            ", startTime=" + startTime +
            ", endTime=" + endTime +
            ", image='" + image + '\'' +
            ", shows=" + shows +
            ", artists=" + artists +
            '}';
    }
}
