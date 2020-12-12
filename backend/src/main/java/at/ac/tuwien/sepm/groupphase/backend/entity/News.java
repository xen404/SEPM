package at.ac.tuwien.sepm.groupphase.backend.entity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "news")
public class News {

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "newsSeen",
        joinColumns = @JoinColumn(name = "news_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id"))
    Set<ApplicationUser> users = new HashSet<>();
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, name = "published_at")
    private LocalDateTime publishedAt;
    @Column(nullable = false, length = 100)
    private String title;
    @Column(nullable = false, length = 500)
    private String summary;
    @Column(nullable = false, length = 100000)
    private String text;
    @Column(length = 1000000)
    private String imagePath;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(LocalDateTime publishedAt) {
        this.publishedAt = publishedAt;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Set<ApplicationUser> getUsers() {
        return users;
    }

    public void setUsers(Set<ApplicationUser> users) {
        this.users = users;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof News)) {
            return false;
        }
        News news = (News) o;
        return Objects.equals(id, news.id)
            && Objects.equals(publishedAt, news.publishedAt)
            && Objects.equals(title, news.title)
            && Objects.equals(summary, news.summary)
            && Objects.equals(text, news.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, publishedAt, title, summary, text);
    }

    @Override
    public String toString() {
        return "Message{"
            + "id=" + id
            + ", publishedAt=" + publishedAt
            + ", title='" + title + '\''
            + ", summary='" + summary + '\''
            + ", text='" + text + '\''
            + '}';
    }


    public static final class NewsBuilder {
        Set<ApplicationUser> users = new HashSet<>();
        private Long id;
        private LocalDateTime publishedAt;
        private String title;
        private String summary;
        private String text;
        private String imagePath;

        private NewsBuilder() {
        }

        public static NewsBuilder newNews() {

            return new NewsBuilder();
        }

        public NewsBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public NewsBuilder withPublishedAt(LocalDateTime publishedAt) {
            this.publishedAt = publishedAt;
            return this;
        }

        public NewsBuilder withTitle(String title) {
            this.title = title;
            return this;
        }

        public NewsBuilder withSummary(String summary) {
            this.summary = summary;
            return this;
        }

        public NewsBuilder withText(String text) {
            this.text = text;
            return this;
        }

        public NewsBuilder withUsers(Set<ApplicationUser> users) {
            this.users = users;
            return this;
        }

        public NewsBuilder withPicture(String imagePath) {
            this.imagePath = imagePath;
            return this;
        }


        public News build() {
            News news = new News();
            news.setId(id);
            news.setPublishedAt(publishedAt);
            news.setTitle(title);
            news.setSummary(summary);
            news.setText(text);
            news.setImagePath(imagePath);
            return news;
        }
    }
}