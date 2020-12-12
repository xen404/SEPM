package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.entity.Ticket;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

public class TicketDto {
    private Long id;
    private Long seatId;
    private char sector;
    private float price;
    private Long orderId;
    private Ticket.Status status;
    private SeatDto seat;
    private String showTitle;
    private LocalTime showStartTime;
    private LocalDate showStartDate;
    private Long showId;

    public TicketDto() {}

    public TicketDto(Long seatId, char sector, float price) {
        this.seatId = seatId;
        this.sector = sector;
        this.price = price;

    }

    public Long getId() {
        return id;
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

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public SeatDto getSeat() {
        return seat;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setSeat(SeatDto seat) {
        this.seat = seat;
    }

    public Ticket.Status getStatus() {
        return status;
    }

    public void setStatus(Ticket.Status status) {
        this.status = status;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getShowTitle() {
        return showTitle;
    }

    public void setShowTitle(String showTitle) {
        this.showTitle = showTitle;
    }

    public LocalTime getShowStartTime() {
        return showStartTime;
    }

    public void setShowStartTime(LocalTime showStartTime) {
        this.showStartTime = showStartTime;
    }

    public LocalDate getShowStartDate() {
        return showStartDate;
    }

    public void setShowStartDate(LocalDate showStartDate) {
        this.showStartDate = showStartDate;
    }

    public Long getShowId() {
        return showId;
    }

    public void setShowId(Long showId) {
        this.showId = showId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TicketDto ticketDto = (TicketDto) o;
        return sector == ticketDto.sector &&
            Float.compare(ticketDto.price, price) == 0 &&
            Objects.equals(seatId, ticketDto.seatId) &&
            Objects.equals(orderId, ticketDto.orderId) &&
            status == ticketDto.status &&
            Objects.equals(seat, ticketDto.seat) &&
            Objects.equals(showTitle, ticketDto.showTitle) &&
            Objects.equals(showStartTime, ticketDto.showStartTime) &&
            Objects.equals(showStartDate, ticketDto.showStartDate) &&
            Objects.equals(showId, ticketDto.showId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(seatId, sector, price, orderId, status, seat, showTitle, showStartTime, showStartDate, showId);
    }

    @Override
    public String toString() {
        return "TicketDto{" +
            "seatId=" + seatId +
            ", sector=" + sector +
            ", price=" + price +
            ", orderId=" + orderId +
            ", status=" + status +
            ", seat=" + seat +
            ", showTitle='" + showTitle + '\'' +
            ", startTime=" + showStartTime +
            ", startDate=" + showStartDate +
            ", showId=" + showId +
            '}';
    }
}
