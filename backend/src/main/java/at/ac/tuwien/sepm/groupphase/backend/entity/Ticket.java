package at.ac.tuwien.sepm.groupphase.backend.entity;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "ticket")
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate dateOfPurchase;

    private LocalDate dateOfReservation;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private ApplicationUser user;

    @ManyToOne
    @JoinColumn(name = "show_id", nullable = false)
    private Show show;

    @ManyToOne
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    public enum Status {
        RESERVED, PURCHASED, FREE;
    }

    @Column(nullable = false)
    private float price;

    // 0 for special sector standing room
    private char sector;

    private Long orderId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ApplicationUser getUser() {
        return user;
    }

    public void setUser(ApplicationUser user) {
        this.user = user;
    }

    public Show getShow() {
        return show;
    }

    public void setShow(Show show) {
        this.show = show;
    }

    public Seat getSeat() {
        return seat;
    }

    public void setSeat(Seat seat) {
        this.seat = seat;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public char getSector() {
        return sector;
    }

    public void setSector(char sector) {
        this.sector = sector;
    }

    public LocalDate getDateOfPurchase() {
        return dateOfPurchase;
    }

    public void setDateOfPurchase(LocalDate dateOfPurchase) {
        this.dateOfPurchase = dateOfPurchase;
    }

    public LocalDate getDateOfReservation() {
        return dateOfReservation;
    }

    public void setDateOfReservation(LocalDate dateOfReservation) {
        this.dateOfReservation = dateOfReservation;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ticket ticket = (Ticket) o;
        return Float.compare(ticket.price, price) == 0 &&
            sector == ticket.sector &&
            id.equals(ticket.id) &&
            Objects.equals(dateOfPurchase, ticket.dateOfPurchase) &&
            Objects.equals(dateOfReservation, ticket.dateOfReservation) &&
            Objects.equals(user, ticket.user) &&
            show.equals(ticket.show) &&
            seat.equals(ticket.seat) &&
            status == ticket.status &&
            Objects.equals(orderId, ticket.orderId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, dateOfPurchase, dateOfReservation, user, show, seat, status, price, sector, orderId);
    }

    @Override
    public String toString() {
        return "Ticket{"
            + "id=" + id
            + ", dateOfPurchase=" + dateOfPurchase
            + ", dateOfReservation=" + dateOfReservation
            + ", user=" + user
            + ", show=" + show
            + ", seat=" + seat
            + ", status=" + status
            + ", price=" + price
            + ", sector=" + sector
            + ", orderId=" + orderId
            + '}';
    }
}
