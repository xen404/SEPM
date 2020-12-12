package at.ac.tuwien.sepm.groupphase.backend.entity;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

public class SaveShow {
    private Show show;
    private List<Ticket> tickets;

    public SaveShow() {
    }

    public SaveShow(Show show, List<Ticket> tickets) {
        this.show = show;
        this.tickets = tickets;
    }

    public Show getShow() {
        return show;
    }

    public void setShow(Show show) {
        this.show = show;
    }

    public List<Ticket> getTickets() {
        return tickets;
    }

    public void setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SaveShow saveShow = (SaveShow) o;
        return show.equals(saveShow.show) &&
            tickets.equals(saveShow.tickets);
    }

    @Override
    public int hashCode() {
        return Objects.hash(show, tickets);
    }
}
