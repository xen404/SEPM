package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import org.springframework.data.domain.Page;

import java.util.List;

public interface LocationService {

    /**
     * Find all location entries ordered by published at date (descending).
     *
     * @return ordered list of all location entries
     */
    List<Location> findAll();

    /**
     * find all location entries.
     *
     * @param page number of page for pagination
     * @return Page Element with locations
     */
    Page<Location> findAll(int page);

    /**
     * Find a single location entry by id.
     *
     * @param id the id of the location entry
     * @return the location entry
     */
    Location findOne(Long id);

    /**
     * Find all Locations with the speficied param.
     *
     * @param description of searched location
     * @param city of searched location
     * @param country of searched location
     * @param street of searched location
     * @param zipcode of searched location
     * @param page for pagination
     * @return a list of locations
     */
    Page<Location> findByParam(String description, String city, String country, String street,
                               String zipcode, int page);

    /**
     * saves a location.
     *
     * @param location which gets saved.
     * @return the saved location
     */
    Location saveLocation(Location location);

    /**
     * get all cities in a list.
     *
     * @return list with all cities
     */
    List<String> getCitiesOrdered();

    /**
     * get all countires in a list.
     *
     * @return list with all countries
     */
    List<String> getCountriesOrdered();

    /**
     * get all zipCodes in a list.
     *
     * @return list with all zipCodes
     */
    List<String> getZipCodesOrdered();

    /**
     * Get all seats of a specific location by id.
     *
     * @param id from the seat
     * @return list of seats of the specific location
     */
    List<Seat> getSeats(Long id);

    List<String> getDescriptionOrdered();
}
