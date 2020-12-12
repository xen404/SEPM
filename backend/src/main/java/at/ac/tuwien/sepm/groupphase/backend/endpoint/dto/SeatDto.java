package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import java.util.Objects;

public class SeatDto {
    private Long id;
    private int rowNr;
    private int seatNr;
    private boolean isRealSeat;

    public SeatDto() {}

    public SeatDto(Long id, int rowNr, int seatNr, boolean isRealSeat) {
        this.id = id;
        this.rowNr = rowNr;
        this.seatNr = seatNr;
        this.isRealSeat = isRealSeat;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getRowNr() {
        return rowNr;
    }

    public void setRowNr(int rowNr) {
        this.rowNr = rowNr;
    }

    public int getSeatNr() {
        return seatNr;
    }

    public void setSeatNr(int seatNr) {
        this.seatNr = seatNr;
    }

    public boolean isRealSeat() {
        return isRealSeat;
    }

    public void setRealSeat(boolean realSeat) {
        isRealSeat = realSeat;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SeatDto seatDto = (SeatDto) o;
        return rowNr == seatDto.rowNr &&
            seatNr == seatDto.seatNr &&
            isRealSeat == seatDto.isRealSeat &&
            id.equals(seatDto.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, rowNr, seatNr, isRealSeat);
    }
}
