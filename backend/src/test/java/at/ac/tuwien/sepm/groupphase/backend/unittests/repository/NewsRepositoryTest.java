package at.ac.tuwien.sepm.groupphase.backend.unittests.repository;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.News;
import at.ac.tuwien.sepm.groupphase.backend.repository.NewsRepository;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@ActiveProfiles("test")
public class NewsRepositoryTest implements TestData {

    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void givenNothing_whenSaveMessage_thenFindListWithOneElementAndFindNewsById() {
        News news = News.NewsBuilder.newNews()
            .withTitle(TEST_NEWS_TITLE)
            .withSummary(TEST_NEWS_SUMMARY)
            .withText(TEST_NEWS_TEXT)
            .withPublishedAt(TEST_NEWS_PUBLISHED_AT)
            .build();

        entityManager.persist(news);

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(newsRepository.findAll().size()).isEqualTo(1);
        softly.assertThat(newsRepository.findById(news.getId())).isNotNull();
        softly.assertAll();
    }

    @Test
    public void givenNews_forCurrentUser_thenFindSeenNewsById() {
        News news = News.NewsBuilder.newNews()
            .withTitle(TEST_NEWS_TITLE)
            .withSummary(TEST_NEWS_SUMMARY)
            .withText(TEST_NEWS_TEXT)
            .withPublishedAt(TEST_NEWS_PUBLISHED_AT)
            .build();


        entityManager.persist(news);

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(newsRepository.findNewsForCurrentUser(1L).size()).isEqualTo(0);
        softly.assertAll();
    }

}
