package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ApplicationUserDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.EditedUserDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;

import at.ac.tuwien.sepm.groupphase.backend.entity.EditedUser;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.util.List;

@Mapper
public interface UserMapper {

    @Named("applicationUser")
    ApplicationUserDto applicationUserToApplicationUserDto(ApplicationUser applicationUser);

    @IterableMapping(qualifiedByName = "applicationUserDtos")
    List<ApplicationUserDto> applicationUserToApplicationUserDto(List<ApplicationUser> applicationUsers);

    @Named("applicationUserDto")
    ApplicationUser applicationUserDtoToApplicationUser(ApplicationUserDto applicationUserDto);

    @IterableMapping(qualifiedByName = "applicationUsers")
    List<ApplicationUser> applicationUserDtoToApplicationUser(List<ApplicationUserDto> applicationUserDtos);

    EditedUser editedUserDtoToEditedUser(EditedUserDto editedUserDto);

}
