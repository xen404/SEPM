package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.News;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.NewsRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UsersRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.NewsService;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class SimpleNewsService implements NewsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final NewsRepository newsRepository;
    private final UsersRepository userRepository;
    private final String uploadDir = Paths.get("").toAbsolutePath().toString();


    public SimpleNewsService(NewsRepository newsRepository, UsersRepository userRepository) {
        this.newsRepository = newsRepository;
        this.userRepository = userRepository;
    }


    @Override
    public News findOne(Long id) {
        LOGGER.debug("Find news with id {}", id);
        Optional<News> news = newsRepository.findById(id);
        if (news.isPresent()) {
            return news.get();
        } else {
            throw new NotFoundException(String.format("Could not find news with id %s", id));
        }
    }

    @Override
    public News setSeenNews(Long id, String userEmail) {

        ApplicationUser user = userRepository.findByEmail(userEmail);
        if (user == null) {
            throw new ValidationException("User for this news does not exist.");
        }

        Optional<News> news = newsRepository.findById(id);

        if (news.isEmpty()) {
            throw new NotFoundException("News not found!");
        }

        newsRepository.deleteSeenNews(user.getId(), id);

        return news.get();
    }

    @Override
    public void uploadImage(Long id, MultipartFile file) {

        Optional<News> news = newsRepository.findById(id);

        if (news.isEmpty()) {
            throw new NotFoundException("No news found!");
        }

        String mimeType = file.getContentType();

        if (mimeType == null) {
            throw new ValidationException("File is not valid.");
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
            news.get().setImagePath(pathdb.toString());
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            LOGGER.debug("Error while uploading the image!" + e.getMessage());
        }
        newsRepository.save(news.get());
    }

    @Override
    public int getLastNews() {
        LOGGER.debug("Get last added news");
        List<News> news = newsRepository.findAll();

        if (news.isEmpty()) {
            throw new NotFoundException("No news available!");
        }

        return news.size();
    }


    @Override
    public String getImage(Long id) {

        Optional<News> news = newsRepository.findById(id);

        if (news.isEmpty()) {
            throw new NotFoundException("News is not found!");
        }

        if (news.get().getImagePath() == null) {
            throw new NotFoundException("Image not found for this news");
        }

        Path path = Paths.get(uploadDir + news.get().getImagePath());


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
            LOGGER.warn("Error while getting the image!");
        }

        return null;
    }


    @Override
    public News publishNews(News news) {
        LOGGER.debug("Publish new news {}", news);
        news.setPublishedAt(LocalDateTime.now());

        List<ApplicationUser> users = userRepository.findAll();

        if (users.isEmpty()) {
            throw new NotFoundException("No users found!");
        }

        for (ApplicationUser user : users) {
            news.getUsers().add(user);
        }

        newsRepository.save(news);

        return news;
    }

    @Override
    public List<News> findLastUnseenNews(String userEmail) {

        LOGGER.debug("Get seen news for current user");

        ApplicationUser user = userRepository.findByEmail(userEmail);
        if (user == null) {
            throw new ValidationException("User for this news does not exist.");
        }

        return newsRepository.findLastUnseenNews(user.getId());
    }

    @Override
    public Page<News> findUnseenNews(String userEmail, Pageable pageable) {
        LOGGER.debug("Get seen news for current user");


        ApplicationUser user = userRepository.findByEmail(userEmail);
        if (user == null) {
            throw new ValidationException("User for this news does not exist.");
        }

        return newsRepository.findUnseenNews(user.getId(), pageable);
    }


    @Override
    public Page<News> findSeenNews(String userEmail, Pageable pageable) {
        LOGGER.debug("Get seen news for current user");

        ApplicationUser user = userRepository.findByEmail(userEmail);
        if (user == null) {
            throw new ValidationException("User for this news does not exist.");
        }

        return newsRepository.findSeenNews(user.getId(), pageable);
    }

}