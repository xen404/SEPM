package at.ac.tuwien.sepm.groupphase.backend.entity;

import java.util.Objects;

public class TopEvent {

    public enum EventCategories {
        CABARET,
        CINEMA,
        CIRCUS,
        CONCERT,
        MUSICAL,
        OPERA,
        THEATRE
    }


    private Long id;
    private String title;
    private Long soldTickets;

    public TopEvent(Long id, String title, Long soldTickets) {
        this.id = id;
        this.title = title;
        this.soldTickets = soldTickets;
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

    public Long getSoldTickets() {
        return soldTickets;
    }

    public void setSoldTickets(Long soldTickets) {
        this.soldTickets = soldTickets;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TopEvent topEvent = (TopEvent) o;
        return Objects.equals(id, topEvent.id) &&
            Objects.equals(title, topEvent.title) &&
            Objects.equals(soldTickets, topEvent.soldTickets);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, soldTickets);
    }

    @Override
    public String toString() {
        return "TopEvent{" +
            "id=" + id +
            ", title='" + title + '\'' +
            ", soldTickets=" + soldTickets +
            '}';
    }
}
