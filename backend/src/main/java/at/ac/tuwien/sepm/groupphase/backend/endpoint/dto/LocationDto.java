package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.entity.Location;

import javax.persistence.Column;
import java.util.Objects;

public class LocationDto {


    private Long id;
    private String description;
    private String street;
    private String city;
    private String country;
    private String zipCode;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LocationDto that = (LocationDto) o;
        return Objects.equals(id, that.id) &&
            Objects.equals(description, that.description) &&
            Objects.equals(street, that.street) &&
            Objects.equals(city, that.city) &&
            Objects.equals(country, that.country) &&
            Objects.equals(zipCode, that.zipCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, description, street, city, country, zipCode);
    }

    @Override
    public String toString() {
        return "LocationDto{" +
            "id=" + id +
            ", description='" + description + '\'' +
            ", street='" + street + '\'' +
            ", city='" + city + '\'' +
            ", country='" + country + '\'' +
            ", zipCode=" + zipCode +
            '}';
    }

    public static final class LocationDtoBuilder {
        private Long id;
        private String description;
        private String street;
        private String city;
        private String country;
        private String zipCode;


        private LocationDtoBuilder() {
        }

        public static LocationDtoBuilder aLocation() {
            return new LocationDtoBuilder();
        }

        public LocationDtoBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public LocationDtoBuilder withdescription(String description) {
            this.description = description;
            return this;
        }

        public LocationDtoBuilder withStreet(String street) {
            this.street = street;
            return this;
        }

        public LocationDtoBuilder withCity(String city) {
            this.city = city;
            return this;
        }

        public LocationDtoBuilder withCountry(String country) {
            this.country = country;
            return this;
        }

        public LocationDtoBuilder withZipCode(String zipCode) {
            this.zipCode = zipCode;
            return this;
        }


        public LocationDto build() {
            LocationDto location = new LocationDto();
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