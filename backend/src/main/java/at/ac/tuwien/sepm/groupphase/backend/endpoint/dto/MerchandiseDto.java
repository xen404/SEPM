package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.util.Objects;

public class MerchandiseDto {


    private Long id;

    @NotNull(message = "Title must not be null")
    private String title;

    private String description;

    @NotNull(message = "Price must not be null")
    private double price;

    @NotNull(message = "Bonus points must not be null")
    private Long bonusPoints;

    private String imagePath;

    @NotNull(message = "Event must not be null")
    private Long eventId;

    @NotNull(message = "Bonus must not be null")
    private boolean bonus;

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

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public boolean isBonus() {
        return bonus;
    }

    public void setBonus(boolean bonus) {
        this.bonus = bonus;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MerchandiseDto that = (MerchandiseDto) o;
        return Double.compare(that.price, price) == 0 &&
            bonus == that.bonus &&
            id.equals(that.id) &&
            title.equals(that.title) &&
            description.equals(that.description) &&
            bonusPoints.equals(that.bonusPoints) &&
            imagePath.equals(that.imagePath) &&
            eventId.equals(that.eventId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, price, bonusPoints, imagePath, eventId, bonus);
    }
}
