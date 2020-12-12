package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import at.ac.tuwien.sepm.groupphase.backend.entity.Merchandise;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.EventRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.MerchandiseRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.MerchandiseService;
import at.ac.tuwien.sepm.groupphase.backend.service.Validator;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class SimpleMerchandiseService implements MerchandiseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final MerchandiseRepository merchandiseRepository;
    private final EventRepository eventRepository;
    private final String uploadDir = Paths.get("").toAbsolutePath().toString();
    private final Validator validator;

    public SimpleMerchandiseService(MerchandiseRepository merchandiseRepository, EventRepository eventRepository, Validator validator) {
        this.merchandiseRepository = merchandiseRepository;
        this.eventRepository = eventRepository;
        this.validator = validator;
    }


    @Override
    public Merchandise save(Merchandise merchandise) {
        LOGGER.debug("Save a merchandise item");
        validator.validateNewMerchandiseItem(merchandise);

        return merchandiseRepository.save(merchandise);
    }

    @Override
    public Page<Merchandise> getAllMerchandiseItems(Pageable pageable) {
        LOGGER.debug("Get all merchandise items");
        return merchandiseRepository.findAll(pageable);
    }

    @Override
    public List<Merchandise> getMerchandiseForEvent(Long eventId) {
        LOGGER.debug("Get all merchandise items for event with id {}", eventId);
        if (eventId <= 0) {
            LOGGER.warn("Event Id must be greater than zero. Currently event id = {}", eventId);
            throw new IllegalArgumentException("Event Id must be greater than zero");
        }
        if (eventRepository.findById(eventId).isPresent()) {
            Event event = eventRepository.getOne(eventId);
            return this.merchandiseRepository.findAllMerchandiseItemsForEvent(event);
        } else {
            LOGGER.warn("Event with id: {} does not exist", eventId);
            throw new NotFoundException("Event with id: " + eventId + " does not exist");
        }
    }

    @Override
    public Page<Merchandise> getAvailableMerchandise(Long bonusPoints, Pageable pageable) {
        LOGGER.debug("Get all available merchandise items for {} bonus points", bonusPoints);
        if (bonusPoints < 0) {
            LOGGER.warn("Bonus Points must me greater than or equal to zero. Currently bonus points = {}", bonusPoints);
            throw new IllegalArgumentException("Bonus Points must me greater than or equal to zero");
        }
        return merchandiseRepository.findMerchandiseLessThanOrEqualToBonusPointsAsc(bonusPoints, pageable);
    }

    @Override
    @Transactional
    public Merchandise getMerchandiseById(Long id) {
        LOGGER.debug("Get merchandise item with id {}", id);
        if (id <= 0) {
            LOGGER.warn("Id must be greater the zero. Currently id = {}", id);
            throw new IllegalArgumentException("Id must be greater the zero");
        }
        if (merchandiseRepository.findById(id).isPresent()) {
            return merchandiseRepository.getOne(id);
        } else {
            LOGGER.warn("Merchandise with id: {} does not exist", id);
            throw new NotFoundException("Merchandise with id: " + id + " does not exist");
        }
    }

    @Override
    public Long getNextIndex() {
        LOGGER.debug("Get next index for merchandise items");
        return (long) merchandiseRepository.findAll().size();
    }

    @Override
    public void uploadImage(Long id, MultipartFile file) {
        LOGGER.debug("Save image to merchandise item with id = {}", id);

        Optional<Merchandise> merchandise = merchandiseRepository.findById(id);

        if(merchandise.isEmpty()) {
            LOGGER.warn("Merchandise with id: {} does not exist", id);
            throw new NotFoundException("Merchandise with id: " + id + " does not exist");
        }

        String mimeType = file.getContentType();

        if (mimeType == null) {
            throw new ValidationException("File is not valid. Cannot read mimeType");
        }

        String type = mimeType.split("/")[0];
        if (!type.equalsIgnoreCase("image")) {
            throw new ValidationException("File is not an image!");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            originalFilename = "";
        }

        Path pathdb = Paths.get(File.separator
            + "src/main/resources/images"
            + File.separator + UUID.randomUUID()
            + "_" + StringUtils.cleanPath(originalFilename));

        Path path = Paths.get(uploadDir + pathdb);

        try {
            merchandise.get().setImagePath(pathdb.toString());
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            merchandiseRepository.save(merchandise.get());
        } catch (IOException e) {
            LOGGER.warn("A problem occurred while trying to write image" + e.getMessage());
        }
    }

    @Override
    public String getImage(Long id) {
        LOGGER.debug("Get image of merchandise item with id = {}", id);
        Optional<Merchandise> merchandise = merchandiseRepository.findById(id);

        if (merchandise.isPresent()) {
            if(merchandise.get().getImagePath() != null) {
                Path path = Paths.get(uploadDir + merchandise.get().getImagePath());

                try {
                    byte[] data = Files.readAllBytes(path);
                    String image = Base64.encodeBase64String(data);

                    if (image.charAt(0) == '/') {
                        image = "data:image/jpeg;base64," + image;
                    } else {
                        if (image.charAt(0) == 'i') {
                            image = "data:image/png;base64," + image;
                        }
                    }
                    return image;

                } catch (IOException e) {
                    LOGGER.warn("A problem occurred while trying to read image");
                    throw new RuntimeException(e);
                }
            } else {
                LOGGER.warn("No image for merchandise with id: {} found.", id);
                throw new NotFoundException("No image for merchandise with id: " + id + " found.");
            }
        } else {
            LOGGER.warn("Merchandise with id: {} does not exist", id);
            throw new NotFoundException("Merchandise with id: " + id + " does not exist");
        }
    }
}
