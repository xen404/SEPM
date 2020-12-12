package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import at.ac.tuwien.sepm.groupphase.backend.entity.Merchandise;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.EventRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class Validator {

    private EventRepository eventRepository;

    public Validator(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public void validateNewMerchandiseItem(Merchandise merchandise) throws ValidationException {
        if (merchandise.getTitle().equals("")) {
            throw new ValidationException("The merchandise title cannot be empty.");
        }
        if (merchandise.getBonusPoints() < 0) {
            throw new ValidationException("The bonus points of a merchandise item must be greater than zero.");
        }
        if (merchandise.getPrice() < 0) {
            throw new ValidationException("The price of a merchandise item must be greater than zero.");
        }
        if (merchandise.getPrice() * 100 != (int) (merchandise.getPrice() * 100)) {
            throw new ValidationException("The price is only allowed to have two decimal places");
        }

        Optional<Event> eventOptional = eventRepository.findById(merchandise.getEvent().getId());
        if (eventOptional.isEmpty()) {
            throw new ValidationException("An event with the given id does not exist.");
        }

        validateImage(merchandise.getImagePath());
    }

    public void validateImage(String imagePath) {

    }

    public void validateUser(ApplicationUser user) throws ValidationException {
        if (user.getEmail().equals("")) {
            throw new ValidationException("The email cannot be empty.");
        }
        if (user.getPassword().equals("")) {
            throw new ValidationException("The password cannot be empty.");
        }
        if (user.getSurname().equals("")) {
            throw new ValidationException("The surname cannot be empty.");
        }
        if (user.getFirstName().equals("")) {
            throw new ValidationException("The first name cannot be empty.");
        }
        if (user.getPassword().length() < 8) {
            throw new ValidationException("The password must be 8 characters long.");
        }
    }
}