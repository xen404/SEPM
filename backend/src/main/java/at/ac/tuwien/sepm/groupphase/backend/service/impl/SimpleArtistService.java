package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.Artist;
import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import at.ac.tuwien.sepm.groupphase.backend.entity.News;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.ArtistRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.EventRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.ArtistService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SimpleArtistService implements ArtistService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final ArtistRepository artistRepository;
    private final EventRepository eventRepository;

    public SimpleArtistService(ArtistRepository artistRepository, EventRepository eventRepository) {
        this.artistRepository = artistRepository;
        this.eventRepository = eventRepository;
    }

    @Override
    public List<Artist> findAllByOrderByName() {
        LOGGER.debug("Find all artists");
        return artistRepository.findAllByOrderByName();
    }

    @Override
    public Page<Artist> findAllByOrderByName(int page) {
        LOGGER.debug("Find all artists");
        Sort sort = Sort.by("name");
        Pageable pageable = PageRequest.of(page, 15, sort);
        Page<Artist> artists = artistRepository.findAllByOrderByName(pageable);
        if (!artists.isEmpty()) {
            return artists;
        } else {
            throw new NotFoundException("No artists were found");
        }
    }

    @Override
    public Artist findOne(Long id) {
        LOGGER.debug("Find artists with id {}", id);
        Optional<Artist> artist = artistRepository.findById(id);
        if (artist.isPresent()) {
            return artist.get();
        } else {
            LOGGER.warn("Could not find message with id {}", id);
            throw new NotFoundException(String.format("Could not find message with id %s", id));
        }
    }

    @Override
    public Page<Artist> findByNameContainingIgnoreCase(String name, Integer page) {
        LOGGER.trace("findByNameContainingIgnoreCase({})", name);
        Sort sort = Sort.by("name");
        Pageable p = PageRequest.of(page, 15, sort);
        Page<Artist> artists = artistRepository.findByNameContainingIgnoreCase(name, p);
        if (!artists.isEmpty()) {
            return artists;
        } else {
            throw new NotFoundException("No artists fitting search criteria were found.");
        }
    }

}
