package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

public class ShowDto {
    private Long id;
    private String title;
    private LocationDto location;
    private String description;
    private LocalTime startTime;
    private LocalTime endTime;
    private Long eventId;
    private LocalDate startDate;
    private LocalDate endDate;
    private int duration;
    private String eventTitle;

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocationDto getLocation() {
        return location;
    }

    public void setLocation(LocationDto location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
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

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShowDto showDto = (ShowDto) o;
        return duration == showDto.duration &&
            Objects.equals(id, showDto.id) &&
            Objects.equals(title, showDto.title) &&
            Objects.equals(location, showDto.location) &&
            Objects.equals(description, showDto.description) &&
            Objects.equals(startTime, showDto.startTime) &&
            Objects.equals(endTime, showDto.endTime) &&
            Objects.equals(eventId, showDto.eventId) &&
            Objects.equals(startDate, showDto.startDate) &&
            Objects.equals(endDate, showDto.endDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, location, description, startTime, endTime, eventId, startDate, endDate, duration);
    }

    @Override
    public String toString() {
        return "ShowDto{" +
            "id=" + id +
            ", title='" + title + '\'' +
            ", location=" + location +
            ", description='" + description + '\'' +
            ", startTime=" + startTime +
            ", endTime=" + endTime +
            ", eventId=" + eventId +
            ", startDate=" + startDate +
            ", endDate=" + endDate +
            ", duration=" + duration +
            '}';
    }
}
