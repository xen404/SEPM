package at.ac.tuwien.sepm.groupphase.backend.unittests.mapping;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.MerchandiseDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.MerchandiseMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.MerchandiseMapperImpl;
import at.ac.tuwien.sepm.groupphase.backend.entity.Merchandise;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = MerchandiseMapperImpl.class)
@ActiveProfiles("test")
public class MerchandiseMappingTest {

    @Autowired
    private MerchandiseMapper merchandiseMapper;

    @Test
    public void givenNothing_whenMerchandiseToMerchandiseDto_thenDtoHasAllProperties() {
        //Arrange
        Merchandise merchandise = TestData.getMerchandise();

        //Act
        MerchandiseDto merchandiseDto = merchandiseMapper.merchandiseToMerchandiseDto(merchandise);

        //Assert
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(merchandiseDto.getTitle()).isEqualTo(merchandise.getTitle());
        softly.assertThat(merchandiseDto.getDescription()).isEqualTo(merchandise.getDescription());
        softly.assertThat(merchandiseDto.getBonusPoints()).isEqualTo(merchandise.getBonusPoints());
        softly.assertThat(merchandiseDto.getPrice()).isEqualTo(merchandise.getPrice());
        softly.assertThat(merchandiseDto.getImagePath()).isEqualTo(merchandise.getImagePath());
        softly.assertThat(merchandiseDto.getEventId()).isEqualTo(merchandise.getEvent().getId());
        softly.assertThat(merchandiseDto.isBonus()).isEqualTo(merchandise.isBonus());
    }

    @Test
    public void givenNothing_whenMerchandiseDtoToMerchandise_thenEntityHasAllProperties() {
        //Arrange
        MerchandiseDto merchandiseDto = TestData.getMerchandiseDto();

        //Act
        Merchandise merchandise = merchandiseMapper.merchandiseDtoToMerchandise(merchandiseDto);

        //Assert
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(merchandiseDto.getTitle()).isEqualTo(merchandise.getTitle());
        softly.assertThat(merchandiseDto.getDescription()).isEqualTo(merchandise.getDescription());
        softly.assertThat(merchandiseDto.getBonusPoints()).isEqualTo(merchandise.getBonusPoints());
        softly.assertThat(merchandiseDto.getPrice()).isEqualTo(merchandise.getPrice());
        softly.assertThat(merchandiseDto.getImagePath()).isEqualTo(merchandise.getImagePath());
        softly.assertThat(merchandiseDto.getEventId()).isEqualTo(merchandise.getEvent().getId());
        softly.assertThat(merchandiseDto.isBonus()).isEqualTo(merchandise.isBonus());
    }
}
