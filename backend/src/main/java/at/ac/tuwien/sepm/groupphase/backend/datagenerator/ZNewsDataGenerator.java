package at.ac.tuwien.sepm.groupphase.backend.datagenerator;

import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.News;
import at.ac.tuwien.sepm.groupphase.backend.repository.NewsRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UsersRepository;
import com.github.javafaker.Faker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.*;
import java.lang.invoke.MethodHandles;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SuppressWarnings("ALL")
@Profile("generateData")
@Component
public class ZNewsDataGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final int NUMBER_OF_MESSAGES_TO_GENERATE = 11;
    private static final String TEST_NEWS_TITLE = "Title";
    private static final String TEST_NEWS_SUMMARY = "Summary of the message";
    private static final String TEST_NEWS_TEXT = "This is the text of the message";
    private static final Long TEST_NEWS_MAX_USER_ID = 62L;
    private final String uploadDir = Paths.get("").toAbsolutePath().toString();
    private ArrayList<String> storeData;


    private final NewsRepository newsRepository;
    private final UsersRepository usersRepository;


    public ZNewsDataGenerator(NewsRepository newsRepository, UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
        this.newsRepository = newsRepository;
    }

    @SuppressWarnings("checkstyle:LeftCurly")
    @PostConstruct
    private void generateMessage() throws IOException {
        if (newsRepository.findAll().size() > 0) {
            LOGGER.debug("message already generated");
        } else {



            LOGGER.debug("generating {} message entries", NUMBER_OF_MESSAGES_TO_GENERATE);
            for (int i = 0; i < NUMBER_OF_MESSAGES_TO_GENERATE; i++) {

                BufferedReader br = new BufferedReader(new InputStreamReader(
                    new FileInputStream(uploadDir + File.separator + "src/main/resources/datagen/news/"
                        + "news" + (i + 1) + ".txt"),  "UTF-8"));
                String line;
                String test = "";
                storeData = new ArrayList<>();
                while ((line = br.readLine()) != null) {
                    if (!line.isEmpty()) {
                        test += test + line;
                    } else {
                        storeData.add(test);
                        test = "";
                    }

                }

                Set<ApplicationUser> users = new HashSet<>();

                for (Long j = 1L; j <= TEST_NEWS_MAX_USER_ID; j++) {

                    users.add(usersRepository.findById(j).get());

                }

                News news = News.NewsBuilder.newNews()
                    .withTitle(storeData.get(0))
                    .withSummary(storeData.get(1))
                    .withText(storeData.get(2))
                    .withPublishedAt(LocalDateTime.now().minusMonths(12 - i))
                    .withUsers(users)
                    .withPicture(File.separator + "src/main/resources/images/" + "news" + (i + 1) + ".jpg")
                    .build();
                LOGGER.debug("saving message {}", news);

                storeData.clear();
                List<ApplicationUser> au = usersRepository.findAll();

                for (ApplicationUser a : au) {
                    news.getUsers().add(a);
                }


                newsRepository.save(news);
            }
        }
    }

}
