package at.ac.tuwien.sepm.groupphase.backend.unittests.mapping;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ApplicationUserDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.EditedUserDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.UserMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.UserMapperImpl;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.EditedUser;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = UserMapperImpl.class)
@ActiveProfiles("test")
public class UserMappingTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void givenNothing_whenUserToUserDto_thenUserDtoHasAllProperties() {
        //Arrange
        ApplicationUser user = TestData.getTestUser();

        //Act
        ApplicationUserDto userDto = userMapper.applicationUserToApplicationUserDto(user);

        //Assert
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(userDto.getId()).isEqualTo(user.getId());
        softly.assertThat(userDto.getEmail()).isEqualTo(user.getEmail());
        softly.assertThat(userDto.getFirstName()).isEqualTo(user.getFirstName());
        softly.assertThat(userDto.getSurname()).isEqualTo(user.getSurname());
        softly.assertThat(userDto.getPassword()).isEqualTo(user.getPassword());
        softly.assertThat(userDto.isAdmin()).isEqualTo(user.getAdmin());
        softly.assertThat(userDto.isAccess()).isEqualTo(user.getAccess());
    }

    @Test
    public void givenNothing_whenUserDtoToUser_thenUserHasAllProperties() {
        //Arrange
        ApplicationUserDto userDto = TestData.getTestUserDto();

        //Act
        ApplicationUser user = userMapper.applicationUserDtoToApplicationUser(userDto);

        //Assert
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(user.getId()).isEqualTo(userDto.getId());
        softly.assertThat(user.getEmail()).isEqualTo(userDto.getEmail());
        softly.assertThat(user.getFirstName()).isEqualTo(userDto.getFirstName());
        softly.assertThat(user.getSurname()).isEqualTo(userDto.getSurname());
        softly.assertThat(user.getPassword()).isEqualTo(userDto.getPassword());
        softly.assertThat(user.getAdmin()).isEqualTo(userDto.isAdmin());
        softly.assertThat(user.getAccess()).isEqualTo(userDto.isAccess());
    }


    @Test
    public void givenNothing_whenEditedUserDtoToEditedUser_thenEditedUserHasAllProperties() {
        //Arrange
        EditedUserDto editedUserDto = TestData.getTestEditedUserDto();

        //Act
        EditedUser editedUser = userMapper.editedUserDtoToEditedUser(editedUserDto);

        //Assert
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(editedUser.getId()).isEqualTo(editedUserDto.getId());
        softly.assertThat(editedUser.getEmail()).isEqualTo(editedUserDto.getEmail());
        softly.assertThat(editedUser.getFirstName()).isEqualTo(editedUserDto.getFirstName());
        softly.assertThat(editedUser.getSurname()).isEqualTo(editedUserDto.getSurname());
        softly.assertThat(editedUser.getPassword()).isEqualTo(editedUserDto.getPassword());
        softly.assertThat(editedUser.getAdmin()).isEqualTo(editedUserDto.isAdmin());
        softly.assertThat(editedUser.getAccess()).isEqualTo(editedUserDto.isAccess());
    }



}
