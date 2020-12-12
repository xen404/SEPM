package at.ac.tuwien.sepm.groupphase.backend.entity;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Table (name = "location")
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "location",
        cascade = CascadeType.ALL,
        orphanRemoval = true)
    private List<Seat> seats;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(
        mappedBy = "location",
        cascade = CascadeType.ALL,
        orphanRemoval = true)
    private List<Show> shows;

    @Column(nullable = false, length = 100)
    private String description;

    @Column(nullable = false, length = 100)
    private String street;

    @Column(nullable = false, length = 100)
    private String city;

    @Column(nullable = false, length = 100)
    private String country;

    @Column(nullable = false)
    private String zipCode;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Seat> getSeats() {
        return seats;
    }

    public void setSeats(List<Seat> seats) {
        this.seats = seats;
    }

    public List<Show> getShows() {
        return shows;
    }

    public void setShows(List<Show> shows) {
        this.shows = shows;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    //Don't compare seats & shows!
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Location location = (Location) o;
        return id.equals(location.id)
            && description.equals(location.description)
            && street.equals(location.street)
            && city.equals(location.city)
            && country.equals(location.country)
            && zipCode.equals(location.zipCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, description, street, city, country, zipCode);
    }

    @Override
    public String toString() {
        return "Location{"
            + "id=" + id
            + ", seats=" + seats
            + ", shows=" + shows
            + ", description='" + description + '\''
            + ", street='" + street + '\''
            + ", city='" + city + '\''
            + ", country='" + country + '\''
            + ", zipCode='" + zipCode + '\''
            + '}';
    }

    public static final class LocationBuilder {
        private Long id;
        private String description;
        private String street;
        private String city;
        private String country;
        private String zipCode;

        private LocationBuilder() {
        }

        public static  LocationBuilder aLocation() {
            return new  LocationBuilder();
        }

        public LocationBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public  LocationBuilder withdescription(String description) {
            this.description = description;
            return this;
        }

        public LocationBuilder withStreet(String street) {
            this.street = street;
            return this;
        }

        public LocationBuilder withCity(String city) {
            this.city = city;
            return this;
        }

        public LocationBuilder withCountry(String country) {
            this.country = country;
            return this;
        }

        public LocationBuilder withZipCode(String zipCode) {
            this.zipCode = zipCode;
            return this;
        }


        public Location build() {
            Location location = new Location();
            location.setId(id);
            location.setCity(city);
            location.setCountry(country);
            location.setDescription(description);
            location.setStreet(street);
            location.setZipCode(zipCode);
            return location;
        }
    }
}
