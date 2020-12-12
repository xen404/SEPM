package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import java.util.List;
import java.util.Objects;

public class OrderTicketsDto {
    private List<Long> seatIds;
    private String mode;

    public OrderTicketsDto() {}

    public OrderTicketsDto(List<Long> seatIds, String mode) {
        this.seatIds = seatIds;
        this.mode = mode;
    }

    public List<Long> getSeatIds() {
        return seatIds;
    }

    public void setSeatIds(List<Long> seatIds) {
        this.seatIds = seatIds;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderTicketsDto that = (OrderTicketsDto) o;
        return seatIds.equals(that.seatIds) &&
            mode.equals(that.mode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(seatIds, mode);
    }

    @Override
    public String toString() {
        return "OrderTicketsDto{"
            + "seats=" + seatIds
            + ", mode=" + mode
            + '}';
    }
}
