package at.ac.tuwien.sepm.groupphase.backend.entity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

//TODO: replace this class with a correct ApplicationUser Entity implementation
@Entity
@Table(name = "users")
public class ApplicationUser {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 20)
    private String firstName;
    @Column(nullable = false, length = 20)
    private String surname;
    @Column(name = "email", nullable = false, length = 50)
    private String email;
    @Column(nullable = false, length = 100)
    private String password;
    @Column(nullable = false)
    private Boolean admin;
    @Column(name = "access", nullable = false)
    private Boolean access;
    @Column(name = "loginCount")
    private int loginCount;
    @Column(nullable = false)
    private Long bonusPoints;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "newsSeen",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "news_id"))
    Set<News> news = new HashSet<>();

    public ApplicationUser(){
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }

    public Boolean getAccess() {
        return access;
    }

    public void setAccess(Boolean access) {
        this.access = access;
    }

    public Set<News> getNews() {
        return news;
    }

    public void setNews(Set<News> news) {
        this.news = news;
    }

    public Long getBonusPoints() {
        return bonusPoints;
    }

    public void setBonusPoints(Long bonusPoints) {
        this.bonusPoints = bonusPoints;
    }

    public int getLoginCount() {
        return loginCount;
    }

    public void setLoginCount(int loginCount) {
        this.loginCount = loginCount;
    }

    public void updateLoginCount() {
        this.loginCount++;
    }

    public void resetLoginCount() {
        this.loginCount = 0;
    }

    public static final class ApplicationUserBuilder {

        private Long id;
        private String firstName;
        private String surname;
        private String email;
        private String password;
        private boolean admin;
        private boolean access;
        private Long bonusPoints;
        private int loginCount;


        public static ApplicationUser.ApplicationUserBuilder anApplicationUser() {
            return new ApplicationUser.ApplicationUserBuilder();
        }

        public ApplicationUser.ApplicationUserBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public ApplicationUser.ApplicationUserBuilder withFirstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public ApplicationUser.ApplicationUserBuilder withSurname(String surname) {
            this.surname = surname;
            return this;
        }

        public ApplicationUser.ApplicationUserBuilder withEmail(String email) {
            this.email = email;
            return this;
        }

        public ApplicationUser.ApplicationUserBuilder withPassword(String password) {
            this.password = password;
            return this;
        }

        public ApplicationUser.ApplicationUserBuilder withAdmin(Boolean admin) {
            this.admin = admin;
            return this;
        }

        public ApplicationUser.ApplicationUserBuilder withAccess(Boolean access) {
            this.access = access;
            return this;
        }

        public ApplicationUser.ApplicationUserBuilder withBonusPoint(Long bonusPoints) {
            this.bonusPoints = bonusPoints;
            return this;
        }

        public ApplicationUser.ApplicationUserBuilder withLoginCount(int loginCount) {
            this.loginCount = loginCount;
            return this;
        }

        public ApplicationUser build() {
            ApplicationUser applicationUser = new ApplicationUser();
            applicationUser.setId(id);
            applicationUser.setFirstName(firstName);
            applicationUser.setSurname(surname);
            applicationUser.setEmail(email);
            applicationUser.setPassword(password);
            applicationUser.setAdmin(admin);
            applicationUser.setAccess(access);
            applicationUser.setBonusPoints(bonusPoints);
            applicationUser.setLoginCount(loginCount);
            return applicationUser;
        }
    }
}