package at.ac.tuwien.sepm.groupphase.backend.unittests.mapping;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.DetailedNewsDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SimpleNewsDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.NewsMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.NewsMapperImpl;
import at.ac.tuwien.sepm.groupphase.backend.entity.News;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = NewsMapperImpl.class)
@ActiveProfiles("test")
public class NewsMappingTest implements TestData {

    private final News news = TestData.getTestNews();
    @Autowired
    private NewsMapper newsMapper;

    @Test
    public void givenNothing_whenMapDetailedMessageDtoToEntity_thenEntityHasAllProperties() {
        //Act
        DetailedNewsDto detailedNewsDto = newsMapper.newsToDetailedNewsDto(news);

        //Assert
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(detailedNewsDto.getId()).isEqualTo(ID);
        softly.assertThat(detailedNewsDto.getTitle()).isEqualTo(TEST_NEWS_TITLE);
        softly.assertThat(detailedNewsDto.getSummary()).isEqualTo(TEST_NEWS_SUMMARY);
        softly.assertThat(detailedNewsDto.getText()).isEqualTo(TEST_NEWS_TEXT);
        softly.assertThat(detailedNewsDto.getPublishedAt()).isEqualTo(TEST_NEWS_PUBLISHED_AT);
        softly.assertAll();
    }

    @Test
    public void givenNothing_whenMapListWithTwoMessageEntitiesToSimpleDto_thenGetListWithSizeTwoAndAllProperties() {
        //Assert
        List<News> news = new ArrayList<>();
        news.add(this.news);
        news.add(this.news);

        //Act & Assert
        List<SimpleNewsDto> simpleNewsDtos = newsMapper.newsToSimpleNewsDto(news);
        assertThat(simpleNewsDtos.size()).isEqualTo(2);
        SimpleNewsDto simpleNewsDto = simpleNewsDtos.get(0);
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(simpleNewsDto.getId()).isEqualTo(ID);
        softly.assertThat(simpleNewsDto.getTitle()).isEqualTo(TEST_NEWS_TITLE);
        softly.assertThat(simpleNewsDto.getSummary()).isEqualTo(TEST_NEWS_SUMMARY);
        softly.assertThat(simpleNewsDto.getPublishedAt()).isEqualTo(TEST_NEWS_PUBLISHED_AT);
        softly.assertAll();
    }
}
