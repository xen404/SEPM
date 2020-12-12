package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.LocationRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.SeatRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.LocationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.*;

@Service
public class SimpleLocationService implements LocationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final LocationRepository locationRepository;
    private final SeatRepository seatRepository;

    public SimpleLocationService(LocationRepository locationRepository, SeatRepository seatRepository) {
        this.locationRepository = locationRepository;
        this.seatRepository = seatRepository;
    }

    @Override
    public List<Location> findAll() {
        LOGGER.debug("Find all locations");
        List<Location> locations = locationRepository.findAll();
        if (!locations.isEmpty()) {
            return locations;
        } else {
            throw new NotFoundException("No locations were found.");
        }
    }

    @Override
    public Page<Location> findAll(int page) {
        LOGGER.debug("Find all locations with pagination");
        Sort sort = Sort.by("description");
        Pageable pageable = PageRequest.of(page, 15, sort);
        Page<Location> locations = locationRepository.findAll(pageable);
        if (!locations.isEmpty()) {
            return locations;
        } else {
            throw new NotFoundException("No locations were found.");
        }
    }

    @Override
    public Location findOne(Long id) {
        LOGGER.debug("Find location with id {}", id);
        Optional<Location> location = locationRepository.findById(id);
        if (location.isPresent()) {
            return location.get();
        } else {
            LOGGER.warn("Could not find location with id {}", id);
            throw new NotFoundException(String.format("Could not find location with id %s", id));
        }
    }

    @Override
    public Page<Location> findByParam(String description, String city, String country, String street, String zipcode, int page) {
        LOGGER.debug("findByParam({})", description, city, country, street, zipcode);
        Sort sort = Sort.by("description");
        Pageable pageable = PageRequest.of(page, 15, sort);
        Page<Location> locations = locationRepository.findByParam(description, city, country, street, zipcode, pageable);
        if (!locations.isEmpty()) {
            return locations;
        } else {
            throw new NotFoundException("No locations fitting search criteria were found.");
        }
    }

    @Override
    public Location saveLocation(Location location) {
        LOGGER.debug("saveLocation()", location);
        return locationRepository.save(location);
    }

    @Override
    public List<String> getCitiesOrdered() {
        LOGGER.debug("getCitiesOrdered()");
        List<String> list = locationRepository.getCitiesOrdered();
        if (!list.isEmpty()) {
            return list;
        } else {
            throw new NotFoundException("No cities were found.");
        }
    }

    @Override
    public List<String> getCountriesOrdered() {
        LOGGER.debug("getCountriesOrdered()");
        List<String> list = locationRepository.getCountriesOrdered();
        if (!list.isEmpty()) {
            return list;
        } else {
            throw new NotFoundException("No countries were found.");
        }
    }

    @Override
    public List<String> getZipCodesOrdered() {
        LOGGER.debug("getZipCodesOrdered()");
        List<String> list = locationRepository.getZipCodeOrdered();
        if (!list.isEmpty()) {
            return list;
        } else {
            throw new NotFoundException("No zip codes were found.");
        }
    }

    @Override
    public List<Seat> getSeats(Long id) {
        LOGGER.debug("getSeats({})", id);
        Optional<Location> location = locationRepository.findById(id);
        if (location.isPresent())  {
            return seatRepository.findByLocation(location.get());
        } else {
            throw new NotFoundException(String.format("Could not find location with id %s", id));
        }
    }

    @Override
    public List<String> getDescriptionOrdered() {
        LOGGER.debug("getZipCodesOrdered()");
        List<String> list = locationRepository.getDescriptionOrdered();
        if (!list.isEmpty()) {
            return list;
        } else {
            throw new NotFoundException("No zip codes were found.");
        }
    }

}
