package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import java.util.List;
import java.util.Objects;

public class SaveShowDto {
    private ShowDto show;
    private List<TicketDto> tickets;

    public SaveShowDto() {}

    public SaveShowDto(ShowDto show, List<TicketDto> tickets) {
        this.show = show;
        this.tickets = tickets;
    }

    public ShowDto getShow() {
        return show;
    }

    public void setShow(ShowDto show) {
        this.show = show;
    }

    public List<TicketDto> getTickets() {
        return tickets;
    }

    public void setTickets(List<TicketDto> tickets) {
        this.tickets = tickets;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SaveShowDto that = (SaveShowDto) o;
        return show.equals(that.show) &&
            tickets.equals(that.tickets);
    }

    @Override
    public int hashCode() {
        return Objects.hash(show, tickets);
    }

    @Override
    public String toString() {
        return "SaveShowDto{" +
            "show=" + show +
            ", tickets=" + tickets +
            '}';
    }
}
