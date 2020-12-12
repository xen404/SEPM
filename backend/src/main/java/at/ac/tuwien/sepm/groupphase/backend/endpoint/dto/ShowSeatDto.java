package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import java.util.Objects;

public class ShowSeatDto {
    private Long seatId;
    private char sector;
    private int rowNr;
    private int seatNr;
    private String status;
    private float price;

    public ShowSeatDto() {}

    public ShowSeatDto(Long seatId, char sector, int rowNr, int seatNr, String status, float price) {
        this.seatId = seatId;
        this.sector = sector;
        this.rowNr = rowNr;
        this.seatNr = seatNr;
        this.status = status;
        this.price = price;
    }

    public Long getSeatId() {
        return seatId;
    }

    public void setSeatId(Long seatId) {
        this.seatId = seatId;
    }

    public char getSector() {
        return sector;
    }

    public void setSector(char sector) {
        this.sector = sector;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShowSeatDto that = (ShowSeatDto) o;
        return sector == that.sector &&
            rowNr == that.rowNr &&
            seatNr == that.seatNr &&
            Float.compare(that.price, price) == 0 &&
            seatId.equals(that.seatId) &&
            status.equals(that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(seatId, sector, rowNr, seatNr, status, price);
    }
}
