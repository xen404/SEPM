package at.ac.tuwien.sepm.groupphase.backend.entity;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "merchandise")
public class Merchandise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(length = 200)
    private String description;

    @Column(nullable = false)
    private double price;

    @Column(nullable = false)
    private Long bonusPoints;

    @Column(nullable = true, length = 1000000)
    private String imagePath;

    @ManyToOne()
    @JoinColumn(name = "event_id")
    private Event event;

    @Column(nullable = false)
    private boolean bonus;

    public Merchandise(){

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Long getBonusPoints() {
        return bonusPoints;
    }

    public void setBonusPoints(Long bonusPoints) {
        this.bonusPoints = bonusPoints;
    }

    public boolean isBonus() {
        return bonus;
    }

    public void setBonus(boolean bonus) {
        this.bonus = bonus;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }


    public static final class MerchandiseBuilder {

        private Long id;
        private String title;
        private String description;
        private double price;
        private Long bonusPoints;
        private boolean bonus;
        private Event event;
        private String imagePath;

        public static Merchandise.MerchandiseBuilder aMerchandise() {
            return new Merchandise.MerchandiseBuilder();
        }

        public Merchandise.MerchandiseBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public Merchandise.MerchandiseBuilder withTitle(String title) {
            this.title = title;
            return this;
        }

        public Merchandise.MerchandiseBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public Merchandise.MerchandiseBuilder withPrice(double price) {
            this.price = price;
            return this;
        }

        public Merchandise.MerchandiseBuilder withBonusPoints(long bonusPoints) {
            this.bonusPoints = bonusPoints;
            return this;
        }


        public Merchandise.MerchandiseBuilder withBonus(boolean bonus) {
            this.bonus = bonus;
            return this;
        }

        public Merchandise.MerchandiseBuilder withEvent(Event event) {
            this.event = event;
            return this;
        }

        public Merchandise.MerchandiseBuilder withPicture(String imagePath) {
            this.imagePath = imagePath;
            return this;
        }


        public Merchandise build() {
            Merchandise merchandise = new Merchandise();
            merchandise.setId(id);
            merchandise.setTitle(title);
            merchandise.setDescription(description);
            merchandise.setPrice(price);
            merchandise.setBonusPoints(bonusPoints);
            merchandise.setBonus(bonus);
            merchandise.setEvent(event);
            merchandise.setImagePath(imagePath);
            return merchandise;
        }

    }
}
