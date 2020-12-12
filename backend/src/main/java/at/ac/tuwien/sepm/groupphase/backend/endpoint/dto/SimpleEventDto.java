package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

public class SimpleEventDto {

    private Long id;

    @NotBlank(message = "Title must not be null")
    @Size(max = 100, message = "Title must not be longer than 100 chars")
    private String title;

    @NotNull(message = "Duration must not be null")
    private int duration;

    @NotNull(message = "start date must not be null")
    private LocalDate startDate;

    @NotNull(message = "end date must not be null")
    private LocalDate endDate;

    private LocalTime startTime;
    private LocalTime endTime;

    private List<ArtistDto> artists;

    public SimpleEventDto() {
    }

    public SimpleEventDto(Long id,
                          @NotBlank(message = "Title must not be null") @Size(max = 100, message = "Title must not be longer than 100 chars") String title,
                          @NotNull(message = "Duration must not be null") int duration, @NotNull(message = "start date must not be null") LocalDate startDate,
                          @NotNull(message = "end date must not be null") LocalDate endDate,
                          List<ArtistDto> artists) {
        this.id = id;
        this.title = title;
        this.duration = duration;
        this.startDate = startDate;
        this.endDate = endDate;
        this.artists = artists;
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

    public List<ArtistDto> getArtists() {
        return artists;
    }

    public void setArtists(List<ArtistDto> artists) {
        this.artists = artists;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleEventDto that = (SimpleEventDto) o;
        return duration == that.duration &&
            Objects.equals(id, that.id) &&
            Objects.equals(title, that.title) &&
            Objects.equals(startDate, that.startDate) &&
            Objects.equals(endDate, that.endDate) &&
            Objects.equals(startTime, that.startTime) &&
            Objects.equals(endTime, that.endTime) &&
            Objects.equals(artists, that.artists);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, duration, startDate, endDate, startTime, endTime, artists);
    }

    @Override
    public String toString() {
        return "SimpleEventDto{" +
            "id=" + id +
            ", title='" + title + '\'' +
            ", duration=" + duration +
            ", startDate=" + startDate +
            ", endDate=" + endDate +
            ", startTime=" + startTime +
            ", endTime=" + endTime +
            ", artists=" + artists +
            '}';
    }
}
