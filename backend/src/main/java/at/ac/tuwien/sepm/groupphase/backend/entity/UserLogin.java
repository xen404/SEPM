package at.ac.tuwien.sepm.groupphase.backend.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Entity
public class UserLogin {

    @Id
    @NotNull(message = "Email must not be null")
    @Email
    private String email;

    @NotNull(message = "Password must not be null")
    private String password;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserLogin)) return false;
        UserLogin userLogin = (UserLogin) o;
        return Objects.equals(email, userLogin.email) &&
            Objects.equals(password, userLogin.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, password);
    }

    @Override
    public String toString() {
        return "UserLogin{" +
            "email='" + email + '\'' +
            ", password='" + password + '\'' +
            '}';
    }

    public static final class UserLoginBuilder {
        private String email;
        private String password;

        private UserLoginBuilder() {
        }

        public static UserLogin.UserLoginBuilder anUserLogin() {
            return new UserLogin.UserLoginBuilder();
        }

        public UserLogin.UserLoginBuilder withEmail(String email) {
            this.email = email;
            return this;
        }

        public UserLogin.UserLoginBuilder withPassword(String password) {
            this.password = password;
            return this;
        }

        public UserLogin build() {
            UserLogin userLogin = new UserLogin();
            userLogin.setEmail(email);
            userLogin.setPassword(password);
            return userLogin;
        }
    }
}
