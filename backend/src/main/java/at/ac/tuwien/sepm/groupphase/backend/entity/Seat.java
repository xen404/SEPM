package at.ac.tuwien.sepm.groupphase.backend.entity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "seat")
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    private int rowNr;

    private int seatNr;

    @Column(nullable = false)
    private boolean isRealSeat;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
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
        Seat seat = (Seat) o;
        return rowNr == seat.rowNr &&
            seatNr == seat.seatNr &&
            isRealSeat == seat.isRealSeat &&
            id.equals(seat.id) &&
            location.equals(seat.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, location, rowNr, seatNr, isRealSeat);
    }

    @Override
    public String toString() {
        return "Seat{"
            + "id=" + id
            + ", location with id='" + location.getId()
            + ", rowNr=" + rowNr
            + ", seatNr=" + seatNr
            + ", isRealSeat=" + isRealSeat
            + '}';
    }
}
