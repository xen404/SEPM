package at.ac.tuwien.sepm.groupphase.backend.datagenerator;

import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;

import at.ac.tuwien.sepm.groupphase.backend.repository.UsersRepository;
import com.github.javafaker.Faker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.PostConstruct;
import java.lang.invoke.MethodHandles;
import java.util.LinkedList;
import java.util.List;

@Profile("generateData")
@Component
public class UserDataGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private static final int NUMBER_OF_USERS_TO_GENERATE = 900;
    private static final int NUMBER_OF_ADMINS_TO_GENERATE = 100;
    private final ApplicationUser testUser;
    private final ApplicationUser testAdmin;
    private List<ApplicationUser> savedUsers = new LinkedList<>();

    @Autowired
    public UserDataGenerator(UsersRepository usersRepository, PasswordEncoder passwordEncoder) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
        this.testUser = ApplicationUser.ApplicationUserBuilder.anApplicationUser()
            .withAccess(true)
            .withAdmin(false)
            .withEmail("max.muster@email.com")
            .withFirstName("Max")
            .withSurname("Muster")
            .withPassword(passwordEncoder.encode("password"))
            .withId(1L)
            .withBonusPoint(100L)
            .withLoginCount(0)
            .build();
        this.testAdmin = ApplicationUser.ApplicationUserBuilder.anApplicationUser()
            .withAccess(true)
            .withAdmin(true)
            .withEmail("anna.anders@email.com")
            .withFirstName("Anna")
            .withSurname("Anders")
            .withPassword(passwordEncoder.encode("password"))
            .withId(2L)
            .withLoginCount(0)
            .withBonusPoint(0L)
            .build();
    }

    @PostConstruct
    private void generateUser() {
        if (usersRepository.findAll().size() > 0) {
            LOGGER.debug("users already generated");
        } else {
            LOGGER.debug("generating {} testUsers entries");
            savedUsers.add(testUser);
            savedUsers.add(testAdmin);

            LOGGER.debug("generating {} user entries");
            boolean access = false;
            for (int i = 0; i < NUMBER_OF_USERS_TO_GENERATE; i++) {
                if (i == NUMBER_OF_USERS_TO_GENERATE / 5) {
                    access = true;
                }
                generate(access, false);
            }
            LOGGER.debug("generating {} admin entries");
            access = false;
            for (int i = 0; i < NUMBER_OF_ADMINS_TO_GENERATE; i++) {
                if (i == NUMBER_OF_ADMINS_TO_GENERATE / 5) {
                    access = true;
                }
                generate(access, true);
            }
            usersRepository.saveAll(savedUsers);
        }
    }

    private void generate(boolean access, boolean admin) {
        Faker faker = new Faker();
        String firstName = faker.name().firstName();
        String lastName = faker.name().lastName();
        String email = firstName.toLowerCase() + "." + lastName.toLowerCase() + "@email.com";
        Long bonusPoints = (long) faker.number().numberBetween(0, 300);


        ApplicationUser applicationUser = ApplicationUser.ApplicationUserBuilder.anApplicationUser()
            .withAdmin(admin)
            .withEmail(email)
            .withFirstName(firstName)
            .withSurname(lastName)
            .withPassword(passwordEncoder.encode("password"))
            .withAccess(access)
            .withLoginCount(0)
            .withBonusPoint(bonusPoints)
            .build();
        LOGGER.debug("saving admin {}", applicationUser);
        savedUsers.add(applicationUser);
    }

}
