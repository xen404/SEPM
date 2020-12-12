package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.entity.EditedUser;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.util.Objects;

public class EditedUserDto {
    @NotNull(message = "ID must not be null")
    private Long id;

    @NotNull(message = "First name must not be null")
    private String firstName;

    @NotNull(message = "Surname must not be null")
    private String surname;

    @NotNull(message = "Email must not be null")
    @Email
    private String email;

    private String password;

    @NotNull(message = "Admin must not be null")
    private boolean admin;

    @NotNull(message = "Access must not be null")
    private boolean access;

    @NotNull(message = "Bonus points must not be null")
    private Long bonusPoints;

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

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public boolean isAccess() {
        return access;
    }

    public void setAccess(boolean access) {
        this.access = access;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EditedUserDto that = (EditedUserDto) o;
        return admin == that.admin
            && access == that.access
            && id.equals(that.id)
            && firstName.equals(that.firstName)
            && surname.equals(that.surname)
            && email.equals(that.email)
            && password.equals(that.password)
            && bonusPoints.equals(that.bonusPoints);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, surname, email, password, admin, access,  bonusPoints);
    }

    @Override
    public String toString() {
        return "ApplicationUserDto{"
            + "id=" + id
            + ", firstName='" + firstName + '\''
            + ", surname='" + surname + '\''
            + ", email='" + email + '\''
            + ", password='" + password + '\''
            + ", admin=" + admin
            + ", access=" + access
            + ", bonusPoints=" + bonusPoints
            + '}';
    }

    public Long getBonusPoints() {
        return bonusPoints;
    }

    public void setBonusPoints(Long bonusPoints) {
        this.bonusPoints = bonusPoints;
    }

    public static final class EditedUserDtoBuilder {
        private Long id;
        private String firstName;
        private String surname;
        private String email;
        private String password;
        private boolean admin;
        private boolean access;
        private Long bonusPoints;

        private EditedUserDtoBuilder() {
        }

        public static EditedUserDto.EditedUserDtoBuilder anEditedUserDto() {
            return new EditedUserDto.EditedUserDtoBuilder();
        }

        public EditedUserDto.EditedUserDtoBuilder withId(long id) {
            this.id = id;
            return this;
        }

        public EditedUserDto.EditedUserDtoBuilder withFirstName(String firstName) {
            this.firstName = firstName;

            return this;
        }

        public EditedUserDto.EditedUserDtoBuilder withSurname(String surname) {
            this.surname = surname;
            return this;
        }

        public EditedUserDto.EditedUserDtoBuilder withEmail(String email) {
            this.email = email;
            return this;
        }

        public EditedUserDto.EditedUserDtoBuilder withPassword(String password) {
            this.password = password;
            return this;
        }

        public EditedUserDto.EditedUserDtoBuilder withAdmin(boolean admin) {
            this.admin = admin;
            return this;
        }

        public EditedUserDto.EditedUserDtoBuilder withAccess(boolean access) {
            this.access = access;
            return this;
        }

        public EditedUserDto.EditedUserDtoBuilder withBonusPoints(Long bonusPoints) {
            this.bonusPoints = bonusPoints;
            return this;
        }

        public EditedUserDto build() {
            EditedUserDto editedUserDto = new EditedUserDto();
            editedUserDto.setFirstName(firstName);
            editedUserDto.setSurname(surname);
            editedUserDto.setEmail(email);
            editedUserDto.setPassword(password);
            editedUserDto.setAdmin(admin);
            editedUserDto.setAccess(access);
            editedUserDto.setBonusPoints(bonusPoints);
            return editedUserDto;
        }
    }
}
