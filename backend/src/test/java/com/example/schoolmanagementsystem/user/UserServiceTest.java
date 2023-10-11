package com.example.schoolmanagementsystem.user;

import com.example.schoolmanagementsystem.AbstractServiceTest;
import com.example.schoolmanagementsystem.auth.AuthenticationUtil;
import com.example.schoolmanagementsystem.exception.NotEnoughAuthorityException;
import com.example.schoolmanagementsystem.exception.ResourceNotFoundException;
import com.example.schoolmanagementsystem.exception.RequestValidationError;
import com.example.schoolmanagementsystem.exception.UserEmailTakeException;
import com.example.schoolmanagementsystem.util.UpdateUtil;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ContextConfiguration(classes = UserService.class)
class UserServiceTest extends AbstractServiceTest {
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private UserDTOMapper userDTOMapper;
    @MockBean
    private PasswordEncoder passwordEncoder;
    @MockBean
    private AuthenticationUtil authenticationUtil;
    @MockBean
    private UpdateUtil updateUtil;
    @Autowired
    private UserService userService;
    @Autowired
    private ResourceLoader resourceLoader;

    private User createUserByRoleWithId(Role role, Long userId) {
        return User.builder()
                .id(userId)
                .role(role)
                .email(FAKER.internet().safeEmailAddress())
                .password(FAKER.crypto().sha256())
                .fullName(FAKER.name().fullName())
                .gender(Gender.MALE)
                .dateOfBirth(LocalDate.now())
                .build();
    }

    @Test
    void generateAnAdminUserIfNone() throws IOException {
        Resource resource = resourceLoader.getResource("classpath:application.properties");
        InputStream inputStream = resource.getInputStream();
        Properties properties = new Properties();
        properties.load(inputStream);

        when(userRepository.existsByRole(Role.ADMIN))
                .thenReturn(false);

        when(passwordEncoder.encode(anyString()))
                .thenReturn(FAKER.internet().password());

        userService.generateAnAdminUserIfNone();

        ArgumentCaptor<User> userArgumentCaptor =
                ArgumentCaptor.forClass(User.class);

        verify(userRepository).existsByRole(Role.ADMIN);

        verify(userRepository).save(userArgumentCaptor.capture());

        assertThat(userArgumentCaptor.getValue().getEmail())
                .isEqualTo(properties.getProperty("admin.email"));
    }

    @Test
    void generateAnAdminUserIfAdminExists() {
        when(userRepository.existsByRole(Role.ADMIN))
                .thenReturn(true);

        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void registerUser() {
        UserRegistrationRequest request =
                new UserRegistrationRequest(
                        Role.STUDENT,
                        FAKER.internet().safeEmailAddress(),
                        FAKER.internet().password(),
                        FAKER.name().fullName(),
                        Gender.MALE,
                        null,
                        LocalDate.now()
                );

        String encodedPassword = FAKER.crypto().sha256();

        when(userRepository.existsByEmail(request.email()))
                .thenReturn(false);

        when(authenticationUtil.isUserPermittedToInteractWith(
                request.role()))
                .thenReturn(true);

        when(passwordEncoder.encode(request.password()))
                .thenReturn(encodedPassword);

        ArgumentCaptor<User> userArgumentCaptor =
                ArgumentCaptor.forClass(User.class);

        User expectedUser = User.builder()
                .role(request.role())
                .email(request.email())
                .password(encodedPassword)
                .fullName(request.fullName())
                .gender(request.gender())
                .mobilePhone(request.mobilePhone())
                .dateOfBirth(request.dateOfBirth())
                .build();

        userService.registerUser(request);

        verify(userRepository).save(userArgumentCaptor.capture());

        assertThat(userArgumentCaptor.getValue())
                .isEqualTo(expectedUser);
    }

    @Test
    void registerUserWithTakenEmail() {
        UserRegistrationRequest request =
                new UserRegistrationRequest(
                        Role.STUDENT,
                        FAKER.internet().safeEmailAddress(),
                        FAKER.internet().password(),
                        FAKER.name().fullName(),
                        Gender.MALE,
                        null,
                        LocalDate.now()
                );

        when(userRepository.existsByEmail(request.email()))
                .thenReturn(true);

        assertThatThrownBy(() -> userService.registerUser(request))
                .isInstanceOf(UserEmailTakeException.class)
                .hasMessage("Provided email is already in use");
    }

    @Test
    void registerUserWithInsufficientAuthority() {
        UserRegistrationRequest request =
                new UserRegistrationRequest(
                        Role.STUDENT,
                        FAKER.internet().safeEmailAddress(),
                        FAKER.internet().password(),
                        FAKER.name().fullName(),
                        Gender.MALE,
                        null,
                        LocalDate.now()
                );

        when(userRepository.existsByEmail(request.email()))
                .thenReturn(false);

        when(authenticationUtil.isUserPermittedToInteractWith(request.role()))
                .thenReturn(false);

        assertThatThrownBy(() -> userService.registerUser(request))
                .isInstanceOf(NotEnoughAuthorityException.class)
                .hasMessage("You don't have the right to create users with this role");
    }

    @Test
    void getUserById() {
        Long userId = FAKER.number().randomNumber();

        User user = createUserByRoleWithId(Role.STUDENT, userId);

        when(userRepository.findById(userId))
                .thenReturn(Optional.ofNullable(user));

        when(authenticationUtil.isUserPermittedToInteractWith(user.getRole()))
                .thenReturn(true);

        ArgumentCaptor<User> userArgumentCaptor =
                ArgumentCaptor.forClass(User.class);

        userService.getUserById(userId);

        verify(userDTOMapper).apply(userArgumentCaptor.capture());

        assertThat(userArgumentCaptor.getValue())
                .isEqualTo(user);
    }

    @Test
    void getUnexistingUserById() {
        Long userId = FAKER.number().randomNumber();

        assertThatThrownBy(() -> userService.getUserById(userId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User with id [%s] does not exist".formatted(userId));
    }

    @Test
    void getUserByIdWithInsufficientAuthority() {
        Long userId = FAKER.number().randomNumber();

        User user = createUserByRoleWithId(Role.STUDENT, userId);

        when(userRepository.findById(userId))
                .thenReturn(Optional.ofNullable(user));

        when(authenticationUtil.isUserPermittedToInteractWith(user.getRole()))
                .thenReturn(false);

        assertThatThrownBy(() -> userService.getUserById(userId))
                .isInstanceOf(NotEnoughAuthorityException.class)
                .hasMessage("You don't have the right to retrieve users with this role");
    }

    @Test
    void getUserByEmail() {
        String email = FAKER.lorem().sentence();

        User user = createUserByRole(Role.STUDENT);

        when(authenticationUtil.isUserAdmin())
                .thenReturn(true);

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.ofNullable(user));

        UserDTO resultUser = userService.getUserByEmail(email);

        assertThat(resultUser).isEqualTo(userDTOMapper.apply(user));
    }

    @Test
    void getUnexistingUserByEmail() {
        String email = FAKER.lorem().sentence();

        when(authenticationUtil.isUserAdmin())
                .thenReturn(true);

        assertThatThrownBy(() -> userService.getUserByEmail(email))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User with email [%s] does not exist".formatted(email));
    }

    @Test
    void getUserByEmailWithInsufficientAuthority() {
        String email = FAKER.lorem().sentence();

        when(authenticationUtil.isUserAdmin())
                .thenReturn(false);

        assertThatThrownBy(() -> userService.getUserByEmail(email))
                .isInstanceOf(NotEnoughAuthorityException.class)
                .hasMessage("You don't have the right to access this resource");
    }



    @Test
    void getAllStudentsWithoutSearch() {
        int pageCount = FAKER.number().numberBetween(1, 10);
        int pageSize = FAKER.number().numberBetween(1, 10);

        List<User> users = List.of(
                createUserByRole(Role.STUDENT),
                createUserByRole(Role.STUDENT)
        );

        when(authenticationUtil.isUserStudent())
                .thenReturn(false);

        Pageable pageable = PageRequest.of(pageCount, pageSize);

        when(userRepository.findByRole(Role.STUDENT, pageable))
                .thenReturn(new PageImpl<>(users, pageable, users.size()));

        List<UserDTO> resultUsers = userService.getAllStudents("", pageCount, pageSize);

        assertThat(resultUsers).containsExactlyElementsOf(users.stream().map(userDTOMapper).collect(Collectors.toList()));
    }

    @Test
    void getAllStudentsWithSearch() {
        int pageCount = FAKER.number().numberBetween(1, 10);
        int pageSize = FAKER.number().numberBetween(1, 10);
        String fullNameSearch = FAKER.lorem().word().substring(0, 1);

        List<User> users = List.of(
                createUserByRole(Role.STUDENT),
                createUserByRole(Role.STUDENT)
        );

        when(authenticationUtil.isUserStudent())
                .thenReturn(false);

        Pageable pageable = PageRequest.of(pageCount, pageSize);

        when(userRepository.findByRoleAndFullNameStartsWithIgnoreCase(Role.STUDENT, fullNameSearch, pageable))
                .thenReturn(new PageImpl<>(users, pageable, users.size()));

        List<UserDTO> resultUsers = userService.getAllStudents(fullNameSearch, pageCount, pageSize);

        assertThat(resultUsers).containsExactlyElementsOf(users.stream().map(userDTOMapper).collect(Collectors.toList()));
    }

    @Test
    void getAllStudentsWithInsufficientAuthority() {
        when(authenticationUtil.isUserStudent())
                .thenReturn(true);

        assertThatThrownBy(() -> userService.getAllStudents(
                null, FAKER.number().randomDigit(), FAKER.number().randomDigit()))
                .isInstanceOf(NotEnoughAuthorityException.class)
                .hasMessage("You don't have the right to access this information");
    }

    @Test
    void getAllTeachersWithoutSearch() {
        int pageCount = FAKER.number().numberBetween(1, 10);
        int pageSize = FAKER.number().numberBetween(1, 10);
        String fullNameSearch = FAKER.lorem().word().substring(0, 1);

        List<User> users = List.of(
                createUserByRole(Role.TEACHER),
                createUserByRole(Role.TEACHER)
        );

        when(authenticationUtil.isUserAdmin())
                .thenReturn(true);

        Pageable pageable = PageRequest.of(pageCount, pageSize);

        when(userRepository.findByRoleAndFullNameStartsWithIgnoreCase(Role.TEACHER, fullNameSearch, pageable))
                .thenReturn(new PageImpl<>(users, pageable, users.size()));

        List<UserDTO> resultUsers = userService.getAllTeachers(fullNameSearch, pageCount, pageSize);

        assertThat(resultUsers).containsExactlyElementsOf(users.stream().map(userDTOMapper).collect(Collectors.toList()));
    }

    @Test
    void getAllTeachersWithSearch() {
        int pageCount = FAKER.number().numberBetween(1, 10);
        int pageSize = FAKER.number().numberBetween(1, 10);

        List<User> users = List.of(
                createUserByRole(Role.TEACHER),
                createUserByRole(Role.TEACHER)
        );


        when(authenticationUtil.isUserAdmin())
                .thenReturn(true);

        Pageable pageable = PageRequest.of(pageCount, pageSize);

        when(userRepository.findByRole(Role.TEACHER, pageable))
                .thenReturn(new PageImpl<>(users, pageable, users.size()));

        List<UserDTO> resultUsers = userService.getAllTeachers("", pageCount, pageSize);

        assertThat(resultUsers).containsExactlyElementsOf(users.stream().map(userDTOMapper).collect(Collectors.toList()));
    }

    @Test
    void getAllTeachersWithInsufficientAuthority() {
        when(authenticationUtil.isUserAdmin())
                .thenReturn(false);

        assertThatThrownBy(() -> userService.getAllTeachers(
                null, FAKER.number().randomDigit(), FAKER.number().randomDigit()))
                .isInstanceOf(NotEnoughAuthorityException.class)
                .hasMessage("You don't have the right to access this information");
    }

    @Test
    void updateUser() {
        User user = createUserByRole(Role.TEACHER);

        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));

        UpdateUserRequest userUpdateRequest =
                new UpdateUserRequest(
                        FAKER.internet().safeEmailAddress(),
                        FAKER.name().fullName(),
                        Gender.MALE,
                        FAKER.phoneNumber().phoneNumber(),
                        LocalDate.now()
                );

        when(authenticationUtil.isUserPermittedToInteractWith(user.getRole()))
                .thenReturn(true);

        when(userRepository.existsByEmail(userUpdateRequest.email()))
                .thenReturn(false);

        ArgumentCaptor<User> userArgumentCaptor =
                ArgumentCaptor.forClass(User.class);

        userService.updateUser(user.getId(), userUpdateRequest);

        verify(userRepository).save(userArgumentCaptor.capture());

        User capturedUser = userArgumentCaptor.getValue();

        assertThat(capturedUser.getEmail()).isEqualTo(userUpdateRequest.email());

        assertThat(capturedUser.getFullName()).isEqualTo(userUpdateRequest.fullName());

        assertThat(capturedUser.getGender()).isEqualTo(userUpdateRequest.gender());

        assertThat(capturedUser.getMobilePhone()).isEqualTo(userUpdateRequest.mobilePhone());

        assertThat(capturedUser.getDateOfBirth()).isEqualTo(userUpdateRequest.dateOfBirth());
    }

    @Test
    void updateUnexistingUser() {
        Long userId = FAKER.number().randomNumber();

        UpdateUserRequest userUpdateRequest =
                new UpdateUserRequest(
                        FAKER.internet().safeEmailAddress(),
                        FAKER.name().fullName(),
                        Gender.MALE,
                        FAKER.phoneNumber().phoneNumber(),
                        LocalDate.now()
                );

        assertThatThrownBy(() -> userService.updateUser(userId, userUpdateRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User with id [%s] does not exist".formatted(userId));
    }

    @Test
    void updateUserWithInsufficientAuthority() {
        User user = createUserByRole(Role.TEACHER);

        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));

        UpdateUserRequest userUpdateRequest =
                new UpdateUserRequest(
                        FAKER.internet().safeEmailAddress(),
                        FAKER.name().fullName(),
                        Gender.MALE,
                        FAKER.phoneNumber().phoneNumber(),
                        LocalDate.now()
                );

        when(authenticationUtil.isUserPermittedToInteractWith(user.getRole()))
                .thenReturn(false);

        assertThatThrownBy(() -> userService.updateUser(user.getId(), userUpdateRequest))
                .isInstanceOf(NotEnoughAuthorityException.class)
                .hasMessage("You don't have the right to retrieve users with this role");
    }

    @Test
    void updateUserWithTakenEmail() {
        User user = createUserByRole(Role.TEACHER);

        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));

        UpdateUserRequest userUpdateRequest =
                new UpdateUserRequest(
                        FAKER.internet().safeEmailAddress(),
                        FAKER.name().fullName(),
                        Gender.MALE,
                        FAKER.phoneNumber().phoneNumber(),
                        LocalDate.now()
                );

        when(authenticationUtil.isUserPermittedToInteractWith(user.getRole()))
                .thenReturn(true);

        when(userRepository.existsByEmail(userUpdateRequest.email()))
                .thenReturn(true);

        assertThatThrownBy(() -> userService.updateUser(user.getId(), userUpdateRequest))
                .isInstanceOf(UserEmailTakeException.class)
                .hasMessage("Provided email is already in use");
    }

    @Test
    void updateUserWithNoChanges() {
        User user = createUserByRole(Role.TEACHER);

        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));

        UpdateUserRequest userUpdateRequest =
                new UpdateUserRequest(
                        user.getEmail(),
                        user.getFullName(),
                        user.getGender(),
                        user.getMobilePhone(),
                        user.getDateOfBirth()
                );

        when(authenticationUtil.isUserPermittedToInteractWith(user.getRole()))
                .thenReturn(true);

        when(updateUtil.isFieldNullOrWithoutChange(any(), any()))
                .thenReturn(true);

        assertThatThrownBy(() -> userService.updateUser(user.getId(), userUpdateRequest))
                .isInstanceOf(RequestValidationError.class)
                .hasMessage("No data changes found");
    }

    @Test
    void deleteUserById() {
        User user = createUserByRole(Role.TEACHER);

        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));

        when(authenticationUtil.isUserPermittedToInteractWith(user.getRole()))
                .thenReturn(true);

        ArgumentCaptor<User> userArgumentCaptor =
                ArgumentCaptor.forClass(User.class);

        userService.deleteUserById(user.getId());

        verify(userRepository).delete(userArgumentCaptor.capture());

        assertThat(userArgumentCaptor.getValue()).isEqualTo(user);
    }

    @Test
    void deleteUnexistingUserById() {
        Long userId = FAKER.number().randomNumber();

        assertThatThrownBy(() -> userService.deleteUserById(userId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User with id [%s] does not exist".formatted(userId));
    }

    @Test
    void deleteUserByIdWithInsufficientAuthority() {
        User user = createUserByRole(Role.TEACHER);

        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));

        when(authenticationUtil.isUserPermittedToInteractWith(user.getRole()))
                .thenReturn(false);

        assertThatThrownBy(() -> userService.deleteUserById(user.getId()))
                .isInstanceOf(NotEnoughAuthorityException.class)
                .hasMessage("You don't have the right to interact with users with this role");
    }
}