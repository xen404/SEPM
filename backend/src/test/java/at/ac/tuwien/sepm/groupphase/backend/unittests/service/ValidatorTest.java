package at.ac.tuwien.sepm.groupphase.backend.unittests.service;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.entity.Merchandise;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.EventRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class ValidatorTest {

    @Mock
    private EventRepository eventRepository;

    private Validator validator;

    @BeforeEach
    public void initMerchandiseService() {
        this.validator = new Validator(eventRepository);
    }

    @Test
    public void givenEmptyTitle_whenSaveMerchandise_thenThrowValidationException(){
        //Arrange
        Merchandise merchandiseToBeSaved = TestData.getMerchandise();
        merchandiseToBeSaved.setTitle("");

        //Act & Assert
        assertThatExceptionOfType(ValidationException.class).isThrownBy(
            () -> validator.validateNewMerchandiseItem(merchandiseToBeSaved)
        );
    }

    @Test
    public void givenNegativePrice_whenSaveMerchandise_thenThrowValidationException(){
        //Arrange
        Merchandise merchandiseToBeSaved = TestData.getMerchandise();
        merchandiseToBeSaved.setPrice(-1.52);

        //Act & Assert
        assertThatExceptionOfType(ValidationException.class).isThrownBy(
            () -> validator.validateNewMerchandiseItem(merchandiseToBeSaved)
        );
    }

    @Test
    public void givenInvalidPrice_whenSaveMerchandise_thenThrowValidationException(){
        //Arrange
        Merchandise merchandiseToBeSaved = TestData.getMerchandise();
        merchandiseToBeSaved.setPrice(1.001);

        //Act & Assert
        assertThatExceptionOfType(ValidationException.class).isThrownBy(
            () -> validator.validateNewMerchandiseItem(merchandiseToBeSaved)
        );
    }

    @Test
    public void givenNegativeBonusPoints_whenSaveMerchandise_thenThrowValidationException(){
        //Arrange
        Merchandise merchandiseToBeSaved = TestData.getMerchandise();
        merchandiseToBeSaved.setBonusPoints(-2L);

        //Act & Assert
        assertThatExceptionOfType(ValidationException.class).isThrownBy(
            () -> validator.validateNewMerchandiseItem(merchandiseToBeSaved)
        );
    }

    @Test
    public void givenNotExistingEvent_whenSaveMerchandise_thenThrowValidationException(){
        //Arrange
        Merchandise merchandiseToBeSaved = TestData.getMerchandise();
        merchandiseToBeSaved.setEvent(TestData.getTestEvent());
        when(eventRepository.findById(anyLong())).thenReturn(Optional.empty());

        //Act & Assert
        assertThatExceptionOfType(ValidationException.class).isThrownBy(
            () -> validator.validateNewMerchandiseItem(merchandiseToBeSaved)
        );
    }
}
