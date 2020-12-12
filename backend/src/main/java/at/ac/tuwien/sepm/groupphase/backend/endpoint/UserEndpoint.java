package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ApplicationUserDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.EditedUserDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.UserMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.EditedUser;
import at.ac.tuwien.sepm.groupphase.backend.exception.IllegalOperationException;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.lang.invoke.MethodHandles;
import java.security.Principal;
import java.util.List;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@CrossOrigin
@RestController
@RequestMapping(value = "/api/v1/users")
public class UserEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final UserService userService;
    private final UserMapper userMapper;

    @Autowired
    public UserEndpoint(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @Secured("ROLE_ADMIN")
    @GetMapping(value = "/access")
    @ApiOperation(value = "Get list of users with certain access", authorizations = {@Authorization(value = "apiKey")})
    public List<ApplicationUserDto> findAllWithAccess(@RequestParam boolean access) {
        LOGGER.info("GET /api/v1/users/access/{}", access);
        return userMapper.applicationUserToApplicationUserDto(userService.findAll(access));
    }

    @Secured("ROLE_ADMIN")
    @GetMapping(value = "/pages/{pageNumber}")
    @ApiOperation(value = "Get list of locked users", authorizations = {@Authorization(value = "apiKey")})
    public Page<ApplicationUserDto> findAll(@PathVariable final Integer pageNumber) {
        LOGGER.info("GET /api/v1/users/pages/" + pageNumber);
        Pageable pageable = createPageRequest(pageNumber);
        Page<ApplicationUser> usersPage = userService.findAll(pageable);
        List<ApplicationUserDto> userDtos = userMapper.applicationUserToApplicationUserDto(usersPage.getContent());
        return new PageImpl<>(userDtos, pageable, usersPage.getTotalElements());
    }

    private Pageable createPageRequest(int pageNumber) {
        return PageRequest.of(pageNumber, 30, Sort.by("email").ascending());
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/search/pages/{pageNumber}")
    @ApiOperation(value = "Get user", authorizations = {@Authorization(value = "apiKey")})
    public Page<ApplicationUserDto> searchUser(@RequestParam String criteria, @PathVariable final Integer pageNumber) {
        LOGGER.info("GET /api/v1/users/search/" + pageNumber);
        Pageable pageable = createPageRequest(pageNumber);
        Page<ApplicationUser> usersPage = userService.searchUser(criteria, pageable);
        List<ApplicationUserDto> userDtos = userMapper.applicationUserToApplicationUserDto(usersPage.getContent());
        return new PageImpl<>(userDtos, pageable, usersPage.getTotalElements());
    }

    @GetMapping(value = "/email")
    public ApplicationUserDto find(@RequestParam String email) {
        LOGGER.info("GET /api/v1/users/{}", email);
        return userMapper.applicationUserToApplicationUserDto(userService.findUserByEmail(email));
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/signUpCustomer")
    public ApplicationUserDto createCustomer(@Valid @RequestBody ApplicationUserDto applicationUserDto) {
        if (applicationUserDto.isAdmin()) {
            throw new
                ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Error: Only admins can create other admins");
        }
        LOGGER.info("POST /api/v1/users/customer body: {}", applicationUserDto);
        return userMapper.applicationUserToApplicationUserDto(
            userService.saveUser(userMapper.applicationUserDtoToApplicationUser(applicationUserDto)));
    }

    @Secured("ROLE_ADMIN")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/signUpUser")
    @ApiOperation(value = "Sign up user", authorizations = {@Authorization(value = "apiKey")})
    public ApplicationUserDto createUser(@Valid @RequestBody ApplicationUserDto applicationUserDto) {
        LOGGER.info("POST /api/v1/users/user body: {}", applicationUserDto);
        return userMapper.applicationUserToApplicationUserDto(
            userService.saveUser(userMapper.applicationUserDtoToApplicationUser(applicationUserDto)));
    }

    @Secured("ROLE_ADMIN")
    @PatchMapping(value = "/lock")
    @ApiOperation(value = "Unlock user", authorizations = {@Authorization(value = "apiKey")})
    public ApplicationUserDto lock(@Valid @RequestBody ApplicationUserDto applicationUserDto) {
        LOGGER.info("PATCH /api/v1/users email: {}/unlock", applicationUserDto.getEmail());
        return userMapper.applicationUserToApplicationUserDto(
            userService.setUserAccess(applicationUserDto.getEmail(), false));
    }

    @Secured("ROLE_ADMIN")
    @PatchMapping(value = "/unlock")
    @ApiOperation(value = "Unlock user", authorizations = {@Authorization(value = "apiKey")})
    public ApplicationUserDto unlock(@Valid @RequestBody ApplicationUserDto applicationUserDto) {
        LOGGER.info("PATCH /api/v1/users email: {}/lock", applicationUserDto.getEmail());
        return userMapper.applicationUserToApplicationUserDto(
            userService.setUserAccess(applicationUserDto.getEmail(), true));
    }

    @Secured("ROLE_USER")
    @DeleteMapping(value = "/{id}")
    @ApiOperation(value = "Delete account", authorizations = {@Authorization(value = "apiKey")})
    public void deleteAccount(@PathVariable("id") Long id, Principal principal) {
        if (id.equals(userService.findUserByEmail(principal.getName()).getId())) {
            LOGGER.info("DELETE /api/v1/users/{}", id);
            this.userService.deleteUserById(id);
        } else {
            LOGGER.warn("Access denied! No permission given. Wrong id");
            throw new IllegalAccessError("Access denied! No permission given. Wrong id");
        }
    }

    @PatchMapping(value = "/{id}")
    @ApiOperation(value = "Update account", authorizations = {@Authorization(value = "apiKey")})
    public ApplicationUserDto updateAccount(@PathVariable("id") Long id,
                                            @Valid @RequestBody EditedUserDto editedUserDto,
                                            Principal principal) {
        if (id.equals(userService.findUserByEmail(principal.getName()).getId())
            || userService.findUserByEmail(principal.getName()).getAdmin()) {
            LOGGER.info("PATCH /api/v1/users/{}", id);
            EditedUser editedUserUser = userMapper.editedUserDtoToEditedUser(editedUserDto);
            return userMapper.applicationUserToApplicationUserDto(
                userService.updateUser(editedUserUser));
        } else {
            LOGGER.warn("Access denied! No permission given. Wrong id");
            throw new IllegalAccessError("Access denied! No permission given. Wrong id");
        }
    }


    @PatchMapping(value = "{id}/bonus_points")
    @ApiOperation(value = "Update bonus points", authorizations = {@Authorization(value = "apiKey")})
    @Transactional
    public ApplicationUserDto updateBonusPoints(@RequestParam(value = "bonus") Long bonusPoints,
                                                @PathVariable("id") Long id, Principal principal) {
        LOGGER.info("Patch /api/v1/users/{}/bonus_points", id);
        if (id.equals(userService.findUserByEmail(principal.getName()).getId())) {
            try {
                return userMapper.applicationUserToApplicationUserDto(userService.updateBonusPoints(id, bonusPoints));
            } catch (IllegalOperationException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error updating bonus points: ", e);
            }
        } else {
            LOGGER.warn("Access denied! No permission given. Wrong id");
            throw new IllegalAccessError("Access denied! No permission given. Wrong id");
        }
    }
}