package at.ac.tuwien.sepm.groupphase.backend.entity;

import javax.persistence.*;

@Entity
public class SoldTicketsPerEvent {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private Long accumulatedSoldTickets;

    public SoldTicketsPerEvent(){}

    public SoldTicketsPerEvent(Long id, String title, String category, Long accumulatedSoldTickets) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.accumulatedSoldTickets = accumulatedSoldTickets;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Long getAccumulatedSoldTickets() {
        return accumulatedSoldTickets;
    }

    public void setAccumulatedSoldTickets(Long accumulatedSoldTickets) {
        this.accumulatedSoldTickets = accumulatedSoldTickets;
    }

    @Override
    public String toString() {
        return "SoldTicketsPerEvent{"
            + "id=" + id
            + ", title='" + title + '\''
            + ", category='" + category + '\''
            + ", accumulatedSoldTickets=" + accumulatedSoldTickets
            + '}';
    }

    public static final class SoldTicketBuilder {
        private Long id;
        private String title;
        private String category;
        private Long accumulatedSoldTickets;

        private SoldTicketBuilder() {}

        public static SoldTicketBuilder aSoldTicket() {
            return new SoldTicketBuilder();
        }

        public SoldTicketBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public SoldTicketBuilder withTitle(String title) {
            this.title = title;
            return this;
        }

        public SoldTicketBuilder withCategory(String category) {
            this.category = category;
            return this;
        }

        public SoldTicketBuilder withAccumulatedSoldTickets(Long accumulatedSoldTickets) {
            this.accumulatedSoldTickets = accumulatedSoldTickets;
            return this;
        }

        public SoldTicketsPerEvent build() {
            SoldTicketsPerEvent soldTicketsPerEvent = new SoldTicketsPerEvent();
            soldTicketsPerEvent.setId(id);
            soldTicketsPerEvent.setTitle(title);
            soldTicketsPerEvent.setCategory(category);
            soldTicketsPerEvent.setAccumulatedSoldTickets(accumulatedSoldTickets);
            return soldTicketsPerEvent;
        }

    }
}
