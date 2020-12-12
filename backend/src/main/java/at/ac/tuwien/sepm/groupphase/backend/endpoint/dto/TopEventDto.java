package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import java.util.Objects;

public class TopEventDto {
    private Long id;
    private String title;
    private Long accumulatedSoldTickets;

    public TopEventDto() {}

    public TopEventDto(Long id, String title, Long accumulatedSoldTickets) {
        this.id = id;
        this.title = title;
        this.accumulatedSoldTickets = accumulatedSoldTickets;
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

    public Long getAccumulatedSoldTickets() {
        return accumulatedSoldTickets;
    }

    public void setAccumulatedSoldTickets(Long accumulatedSoldTickets) {
        this.accumulatedSoldTickets = accumulatedSoldTickets;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TopEventDto that = (TopEventDto) o;
        return Objects.equals(id, that.id) &&
            Objects.equals(title, that.title) &&
            Objects.equals(accumulatedSoldTickets, that.accumulatedSoldTickets);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, accumulatedSoldTickets);
    }

    @Override
    public String toString() {
        return "TopEventDto{" +
            "id=" + id +
            ", title='" + title + '\'' +
            ", accumulatedSoldTickets=" + accumulatedSoldTickets +
            '}';
    }
}
