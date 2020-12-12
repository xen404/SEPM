package at.ac.tuwien.sepm.groupphase.backend.unittests.service;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import at.ac.tuwien.sepm.groupphase.backend.entity.Merchandise;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.EventRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.MerchandiseRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.MerchandiseService;
import at.ac.tuwien.sepm.groupphase.backend.service.Validator;
import at.ac.tuwien.sepm.groupphase.backend.service.impl.SimpleMerchandiseService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class MerchandiseServiceTest {

    @Mock
    private MerchandiseRepository merchandiseRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private Validator validator;

    private MerchandiseService merchandiseService;
    private Merchandise merchandise;

    @BeforeEach
    public void initMerchandiseService() {
        merchandiseService = new SimpleMerchandiseService(merchandiseRepository, eventRepository, validator);
        merchandise = TestData.getMerchandise();
    }

    @Test
    public void givenValidBonusPoints_whenGetAvailableMerchandise_thenCorrectMerchandiseReturned(){
        //Arrange
        Long bonusPoints = 100L;
        List<Merchandise> bonuses = TestData.getRandomBonusItem(5, bonusPoints);
        Page<Merchandise> page = new PageImpl<Merchandise>(
            bonuses, PageRequest.of(0, 50), bonuses.size());
        when(merchandiseRepository.findMerchandiseLessThanOrEqualToBonusPointsAsc(bonusPoints, PageRequest.of(0, 50))).thenReturn(page);


        //Act
        Page<Merchandise> availableBonuses = merchandiseService.getAvailableMerchandise(bonusPoints, PageRequest.of(0, 50));

        //Assert
        assertThat(availableBonuses).containsOnlyElementsOf(bonuses);
    }

    @Test
    public void givenInvalidBonusPoints_whenGetAvailableMerchandise_thenIllegalArgumentException(){
        //Arrange
        Long illegalBonusPoints = -1L;


        //Act & Assert
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(
            () -> merchandiseService.getAvailableMerchandise(illegalBonusPoints, null)
        );
    }

    @Test
    public void givenExistingEvent_whenGetMerchandiseForEvent_thenCorrectMerchandiseReturned() {
        //Arrange
        Event existingEvent = TestData.getTestEventWithId(2L);
        List <Merchandise> merchandisesForExistingEvent = TestData.getRandomMerchandise(3);
        for (Merchandise m : merchandisesForExistingEvent) {
            m.setEvent(existingEvent);
        }
        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(existingEvent));
        when(merchandiseRepository.findAllMerchandiseItemsForEvent(any())).thenReturn(merchandisesForExistingEvent);

        //Act
        List <Merchandise> merchandiseForGivenEvent = merchandiseService.getMerchandiseForEvent(existingEvent.getId());

        //Assert
        assertThat(merchandiseForGivenEvent).containsOnlyElementsOf(merchandisesForExistingEvent);
    }

    @Test
    public void givenNotExistingEvent_whenGetMerchandiseForEvent_thenThrowNotFoundException() {
        //Arrange
        Long notExistingEventId = 1L;

        //Act & Assert
        assertThatExceptionOfType(NotFoundException.class).isThrownBy(
            () -> merchandiseService.getMerchandiseForEvent(notExistingEventId)
        );
    }

    @Test
    public void givenInvalidId_whenGetMerchandiseById_thenThrowIllegalArgumentException(){
        //Arrange
        Long illegalMerchandiseId = 0L;

        //Act & Assert
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(
            () -> merchandiseService.getMerchandiseById(illegalMerchandiseId)
        );
    }

    @Test
    public void givenNotExistingId_whenGetMerchandiseById_thenThrowNotFoundException(){
        //Arrange
        Long notExistingMerchandiseId = 2L;

        //Act & Assert
        assertThatExceptionOfType(NotFoundException.class).isThrownBy(
            () -> merchandiseService.getMerchandiseById(notExistingMerchandiseId)
        );
    }

    @Test
    public void givenExistingId_whenGetMerchandiseById_thenCorrectMerchandiseReturned(){
        //Arrange
        Merchandise existingMerchandise = TestData.getMerchandise();
        when(merchandiseRepository.findById(anyLong())).thenReturn(Optional.of(existingMerchandise));
        when(merchandiseRepository.getOne(anyLong())).thenReturn(existingMerchandise);

        //Act
        Merchandise returnedMerchandise = merchandiseService.getMerchandiseById(existingMerchandise.getId());

        //Assert
        assertEquals(returnedMerchandise, existingMerchandise);
    }
}
