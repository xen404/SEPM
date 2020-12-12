package at.ac.tuwien.sepm.groupphase.backend.entity;

import java.util.List;
import java.util.Objects;

public class SaveTickets {
    private ApplicationUser user;
    private List<Long> seatIds;

    public SaveTickets() {
    }

    public SaveTickets(ApplicationUser user, List<Long> seatIds) {
        this.user = user;
        this.seatIds = seatIds;
    }

    public ApplicationUser getUser() {
        return user;
    }

    public void setUser(ApplicationUser user) {
        this.user = user;
    }

    public List<Long> getSeatIds() {
        return seatIds;
    }

    public void setSeatIds(List<Long> seatIds) {
        this.seatIds = seatIds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SaveTickets that = (SaveTickets) o;
        return user.equals(that.user) &&
            seatIds.equals(that.seatIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, seatIds);
    }
}
