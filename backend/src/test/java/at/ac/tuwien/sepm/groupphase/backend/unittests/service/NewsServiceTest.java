package at.ac.tuwien.sepm.groupphase.backend.unittests.service;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.News;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.NewsRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UsersRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.NewsService;
import at.ac.tuwien.sepm.groupphase.backend.service.impl.SimpleNewsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;


import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class NewsServiceTest implements TestData {

    @Mock
    private NewsRepository newsRepository;

    @Mock
    private UsersRepository userRepository;

    private NewsService newsService;

    @BeforeEach
    public void initMessageService() {
        newsService = new SimpleNewsService(newsRepository,userRepository);
    }




    @Test
    public void givenNothing_whenFindOne_thenThrowsNotFoundException() {
        //Arrange
        when(newsRepository.findById(anyLong())).thenReturn(Optional.empty());

        //Act & Assert
        assertThatExceptionOfType(NotFoundException.class).isThrownBy(
            () -> newsService.findOne(1L));
    }

    @Test
    public void findUnseenNews_notUnseen_thenreturnUnseenNews() {

        when(userRepository.findByEmail(DEFAULT_USER)).thenReturn(TestData.getTestUser());
        ApplicationUser user = userRepository.findByEmail(DEFAULT_USER);

        News news = TestData.getTestNewsWithId(2L);

        List<News> newsList = new ArrayList<>();
        newsList.add(news);
        newsList.add(TestData.getTestNews());

        Page<News> page = new PageImpl<>(newsList);

        when(newsRepository.findUnseenNews(user.getId(), PageRequest.of(0, 50))).thenReturn(page);
        //Act
        Page<News> unseenNews = newsService.findUnseenNews(user.getEmail(), PageRequest.of(0, 50));

        //Assert
        assertThat(unseenNews).containsOnlyElementsOf(newsList);


    }

    @Test
    public void findSeenNews_notSeen_thenreturnSeenNews() {

        List<ApplicationUser> users = new ArrayList<>();
        users.add(TestData.getTestUser());
        when(userRepository.findAll()).thenReturn(users);

        News news = TestData.getTestNewsWithId(2L);

        when(newsRepository.save(any(News.class))).then(returnsFirstArg());

        when(userRepository.findByEmail(DEFAULT_USER)).thenReturn(TestData.getTestUser());
        ApplicationUser user = userRepository.findByEmail(DEFAULT_USER);

        newsService.publishNews(news);
        newsService.publishNews(TestData.getTestNews());

        List<News> unseen = new ArrayList<>();
        unseen.add(TestData.getTestNews());
        unseen.add(news);

        //Act
        Page<News> page = new PageImpl<>(unseen);

        when(newsRepository.findUnseenNews(user.getId(), PageRequest.of(0, 50))).thenReturn(page);
        newsRepository.deleteSeenNews(user.getId(), news.getId());
        //Act
        Page<News> unseenNews = newsService.findUnseenNews(user.getEmail(), PageRequest.of(0, 50));

        //Assert
        assertThat(unseenNews).containsOnlyElementsOf(unseen);


    }

   @Test
    public void givenNothing_whenMessagePublished_thenMessageHasPublishedAt() {
        //Arrange

        List<ApplicationUser> users = new ArrayList<>();
        users.add(TestData.getTestUser());
        when(userRepository.findAll()).thenReturn(users);

        News news = TestData.getTestNewsWithId(2L);

        when(newsRepository.save(any(News.class))).then(returnsFirstArg());


        //Act
        News publishedNews = newsService.publishNews(news);

        //Assert
        assertThat(publishedNews.getPublishedAt()).isNotNull();
        }


    @Test
    public void findLastSeenNews_notSeen_thenreturnLastThreeSeenNews() {

        List<ApplicationUser> users = new ArrayList<>();
        users.add(TestData.getTestUser());
        when(userRepository.findAll()).thenReturn(users);

        News news = TestData.getTestNewsWithId(2L);
        News news2 = TestData.getTestNewsWithId(3L);


        when(newsRepository.save(any(News.class))).then(returnsFirstArg());

        when(userRepository.findByEmail(DEFAULT_USER)).thenReturn(TestData.getTestUser());
        ApplicationUser user = userRepository.findByEmail(DEFAULT_USER);

        newsService.publishNews(news);
        newsService.publishNews(TestData.getTestNews());

        List<News> seen = new ArrayList<>();
        seen.add(news);
        seen.add(TestData.getTestNews());
        seen.add(news2);


        when(newsRepository.findLastUnseenNews(user.getId())).thenReturn(seen);
        //Act
        List<News> last  = newsService.findLastUnseenNews(user.getEmail());

        //Assert
        assertThat(last.size()).isEqualTo(3);


    }

    @Test
    public void uploadImage_validMultipartfile()
    {
        News news = TestData.getTestNews();
        when(newsRepository.findById(news.getId())).thenReturn(Optional.of(news));

        MockMultipartFile firstFile = new MockMultipartFile("data", "photo.png", "image/png", "some xml".getBytes());

        newsService.uploadImage(news.getId() ,firstFile);

    }

    @Test
    public void uploadImage_notValidNews_thenthrowNotFoundException()
    {
        MockMultipartFile firstFile = new MockMultipartFile("data", "filename.txt", "text/plain", "some xml".getBytes());

        News news = TestData.getTestNews();
        when(newsRepository.findById(news.getId())).thenReturn(Optional.empty());

        //Act & Assert
        assertThatExceptionOfType(NotFoundException.class).isThrownBy(
            () -> newsService.uploadImage(news.getId(), firstFile)
        );

    }

    @Test
    public void getImage_ImagePath_NotFound_thenThrowNotFoundException()
    {
        News news = TestData.getTestNews();
        when(newsRepository.findById(news.getId())).thenReturn(Optional.of(news));

        //Act & Assert
        assertThatExceptionOfType(NotFoundException.class).isThrownBy(
            () -> newsService.getImage(news.getId())
        );

    }

    @Test void getLastNewsofTwoListNews_thenReturnLastElement()
    {
        News news = TestData.getTestNewsWithId(2L);

        List<News> newsList = new ArrayList<>();
        newsList.add(news);
        newsList.add(TestData.getTestNews());

        when(newsRepository.findAll()).thenReturn(newsList);

        int sizeNews = newsService.getLastNews();

        assertThat(sizeNews).isEqualTo(2);
    }

}
