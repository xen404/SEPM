package at.ac.tuwien.sepm.groupphase.backend.unittests.repository;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.entity.Merchandise;
import at.ac.tuwien.sepm.groupphase.backend.repository.MerchandiseRepository;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@ActiveProfiles("test")
public class MerchandiseRepositoryTest {

    @Autowired
    private MerchandiseRepository merchandiseRepository;


    @Test
    public void givenValidBonusPoints_whenFindMerchandiseLessThanOrEqualToBonusPointsAsc_thenCorrectMerchandiseReturned(){
        //Arrange
        Long bonusPoints = 99L;
        List<Merchandise> bonusesLessThan100 = TestData.getRandomBonusItem(5,  bonusPoints);
        List<Merchandise> bonusesMoreThan100 = TestData.getRandomBonusItem(5, bonusPoints);
        List<Merchandise> merchandisesNotBonus = TestData.getRandomMerchandise(2);
        for (Merchandise m: bonusesMoreThan100) {
            m.setBonusPoints(m.getBonusPoints()+100);
        }
        merchandiseRepository.saveAll(bonusesLessThan100);
        merchandiseRepository.saveAll(bonusesMoreThan100);
        merchandiseRepository.saveAll(merchandisesNotBonus);

        //Act
        List<Merchandise> returnedMerchandise = merchandiseRepository.findMerchandiseLessThanOrEqualToBonusPointsAsc(bonusPoints);

        //Assert
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(returnedMerchandise.size()).isEqualTo(5);
        softly.assertThat(returnedMerchandise.containsAll(bonusesLessThan100));
    }
}
