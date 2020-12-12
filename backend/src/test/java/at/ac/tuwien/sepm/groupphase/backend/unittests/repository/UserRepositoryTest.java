package at.ac.tuwien.sepm.groupphase.backend.unittests.repository;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.repository.UsersRepository;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.Assert.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@ActiveProfiles("test")
public class UserRepositoryTest implements TestData {

    @Autowired
    private UsersRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;


    @Test
    public void givenNothing_whenSaveUser_thenFindListWithOneElementAndFindNewsUserByEmail() {
        ApplicationUser user = TestData.getTestUser();

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(userRepository.findAll().size()).isEqualTo(0);

        userRepository.save(user);

        softly.assertThat(userRepository.findAllByOrderByFirstNameDesc(true).size()).isEqualTo(1);
        softly.assertThat(userRepository.findByEmail(user.getEmail())).isNotNull();
        softly.assertAll();
    }

    @Test
    public void givenOneUser_whenUpdateAccess_thenFindListWithFalseAccessWithOneElement() {
        ApplicationUser user = TestData.getTestUser();

        userRepository.save(user);

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(userRepository.findAllByOrderByFirstNameDesc(false).size()).isEqualTo(0);
        softly.assertThat(userRepository.findAllByOrderByFirstNameDesc(true).size()).isEqualTo(1);

        userRepository.updateAccess(false, user.getEmail());

        softly.assertThat(userRepository.findAllByOrderByFirstNameDesc(false).size()).isEqualTo(1);
        softly.assertThat(userRepository.findAllByOrderByFirstNameDesc(true).size()).isEqualTo(0);
        softly.assertAll();
    }
}
