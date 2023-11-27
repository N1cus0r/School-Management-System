package com.example.schoolmanagementsystem.user;

import com.example.schoolmanagementsystem.AbstractCourseRelatedServiceTest;
import com.example.schoolmanagementsystem.AbstractServiceTest;
import com.example.schoolmanagementsystem.auth.AuthenticationUtil;
import com.example.schoolmanagementsystem.course.Course;
import com.example.schoolmanagementsystem.exception.NotEnoughAuthorityException;
import com.example.schoolmanagementsystem.exception.RequestValidationError;
import com.example.schoolmanagementsystem.exception.ResourceNotFoundException;
import com.example.schoolmanagementsystem.exception.UserEmailTakeException;
import com.example.schoolmanagementsystem.homework.Homework;
import com.example.schoolmanagementsystem.homework.HomeworkDTO;
import com.example.schoolmanagementsystem.s3.S3Bucket;
import com.example.schoolmanagementsystem.s3.S3Service;
import com.example.schoolmanagementsystem.util.UpdateUtil;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ContextConfiguration(classes = UserService.class)
class UserServiceTest extends AbstractCourseRelatedServiceTest {
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
    @MockBean
    private S3Service s3Service;
    @MockBean
    private S3Bucket s3Bucket;
    @Autowired
    private UserService userService;

    @Test
    void generateAnAdminUserIfNone() throws IOException {
        when(userRepository.existsByRole(Role.ADMIN))
                .thenReturn(false);

        when(passwordEncoder.encode(anyString()))
                .thenReturn(FAKER.internet().password());

        userService.generateAnAdminUserIfNone();

        ArgumentCaptor<User> userArgumentCaptor =
                ArgumentCaptor.forClass(User.class);

        verify(userRepository).existsByRole(Role.ADMIN);

        verify(userRepository).save(userArgumentCaptor.capture());
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

        when(authenticationUtil.isUserPermittedToCreateUsersWithRole(request.role()))
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

        when(authenticationUtil.isUserPermittedToCreateUsersWithRole(request.role()))
                .thenReturn(false);

        assertThatThrownBy(() -> userService.registerUser(request))
                .isInstanceOf(NotEnoughAuthorityException.class)
                .hasMessage("You don't have the right to create users with provided role");
    }

    @Test
    void getUserByEmail() {
        String email = FAKER.lorem().sentence();

        User user = createUserByRole(Role.STUDENT);

        when(authenticationUtil.isUserPermittedToGetUser(user))
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

        User user = createUserByRole(Role.STUDENT);

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.ofNullable(user));

        when(authenticationUtil.isUserPermittedToGetUser(user))
                .thenReturn(false);
        assertThatThrownBy(() -> userService.getUserByEmail(email))
                .isInstanceOf(NotEnoughAuthorityException.class)
                .hasMessage("You don't have the right retrieve this users information");
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

        when(authenticationUtil.isUserPermittedToUpdateUser(user))
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

        when(authenticationUtil.isUserPermittedToUpdateUser(user))
                .thenReturn(false);

        assertThatThrownBy(() -> userService.updateUser(user.getId(), userUpdateRequest))
                .isInstanceOf(NotEnoughAuthorityException.class)
                .hasMessage("You don't have the right to perform this operation");
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

        when(authenticationUtil.isUserPermittedToUpdateUser(user))
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

        when(authenticationUtil.isUserPermittedToUpdateUser(user))
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

        String userProfileImageId = UUID.randomUUID().toString();

        user.setProfileImageId(userProfileImageId);

        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));

        when(authenticationUtil.isUserPermittedToDeleteUser(user))
                .thenReturn(true);

        String bucketName = FAKER.lorem().word();

        when(s3Bucket.getName())
                .thenReturn(bucketName);

        userService.deleteUserById(user.getId());

        ArgumentCaptor<User> userArgumentCaptor =
                ArgumentCaptor.forClass(User.class);

        verify(s3Service).deleteObject(
                bucketName,
                s3Bucket.PROFILE_IMAGE_PATH.formatted(user.getId(), user.getProfileImageId())
        );

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

        when(authenticationUtil.isUserPermittedToDeleteUser(user))
                .thenReturn(false);

        assertThatThrownBy(() -> userService.deleteUserById(user.getId()))
                .isInstanceOf(NotEnoughAuthorityException.class)
                .hasMessage("You don't have the right to delete this user");
    }


    @Test
    void uploadUserProfileImage() {
        Long userId = FAKER.number().randomNumber();

        User admin = createUserByRole(Role.ADMIN);

        when(userRepository.findById(userId))
                .thenReturn(Optional.ofNullable(admin));

        when(authenticationUtil.isUserPermittedToUpdateUser(admin))
                .thenReturn(true);

        String bucketName = FAKER.lorem().word();

        when(s3Bucket.getName())
                .thenReturn(bucketName);

        byte[] bytes = FAKER.lorem().word().getBytes();

        MultipartFile multipartFile =
                new MockMultipartFile("file", bytes);

        userService.uploadUserProfileImage(userId, multipartFile);

        ArgumentCaptor<String> profileImagePathArgumentCaptor =
                ArgumentCaptor.forClass(String.class);

        verify(s3Service)
                .putObject(
                        eq(bucketName),
                        profileImagePathArgumentCaptor.capture(),
                        eq(bytes)
                );

        ArgumentCaptor<User> userArgumentCaptor =
                ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(userArgumentCaptor.capture());

        assertThat(profileImagePathArgumentCaptor.getValue())
                .contains(userArgumentCaptor.getValue().getProfileImageId())
                .contains(userId.toString());
    }

    @Test
    void uploadUserProfileImageWithInsufficientAuthority() {
        Long userId = FAKER.number().randomNumber();

        User student = createUserByRole(Role.STUDENT);

        when(userRepository.findById(userId))
                .thenReturn(Optional.ofNullable(student));

        when(authenticationUtil.isUserPermittedToUpdateUser(student))
                .thenReturn(false);

        String bucketName = FAKER.lorem().word();

        when(s3Bucket.getName())
                .thenReturn(bucketName);

        byte[] bytes = FAKER.lorem().word().getBytes();

        MultipartFile multipartFile =
                new MockMultipartFile("file", bytes);

        assertThatThrownBy(() -> userService.uploadUserProfileImage(userId, multipartFile))
                .isInstanceOf(NotEnoughAuthorityException.class)
                .hasMessage("You don't have the right to update this user");
    }

    @Test
    void uploadUnexistingUserProfileImage() {
        Long userId = FAKER.number().randomNumber();

        String bucketName = FAKER.lorem().word();

        when(s3Bucket.getName())
                .thenReturn(bucketName);

        byte[] bytes = FAKER.lorem().word().getBytes();

        MultipartFile multipartFile =
                new MockMultipartFile("file", bytes);

        assertThatThrownBy(() -> userService.uploadUserProfileImage(userId, multipartFile))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User with id [%s] does not exist".formatted(userId));
    }

    @Test
    void getCustomerImage() {
        Long userId = FAKER.number().randomNumber();

        User admin = createUserByRole(Role.ADMIN);

        when(userRepository.findById(userId))
                .thenReturn(Optional.ofNullable(admin));

        when(authenticationUtil.isUserPermittedToGetUser(admin))
                .thenReturn(true);

        admin.setProfileImageId(UUID.randomUUID().toString());

        String bucketName = FAKER.lorem().word();

        when(s3Bucket.getName()).thenReturn(bucketName);

        byte[] expectedImage = FAKER.lorem().word().getBytes();

        when(s3Service.getObject(eq(bucketName), any()))
                .thenReturn(expectedImage);

        byte[] userImage = userService.getUserImage(userId);

        assertThat(userImage).isEqualTo(expectedImage);
    }

    @Test
    void getCustomerUnexistingImage() {
        Long userId = FAKER.number().randomNumber();

        User admin = createUserByRole(Role.ADMIN);

        when(userRepository.findById(userId))
                .thenReturn(Optional.ofNullable(admin));

        when(authenticationUtil.isUserPermittedToGetUser(admin))
                .thenReturn(true);

        assertThatThrownBy(() -> userService.getUserImage(userId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User with id [%s] profile image not found".formatted(userId));
    }
    @Test
    void getCustomerImageWithInsufficientAuthority() {
        Long userId = FAKER.number().randomNumber();

        User student = createUserByRole(Role.STUDENT);

        when(userRepository.findById(userId))
                .thenReturn(Optional.ofNullable(student));

        when(authenticationUtil.isUserPermittedToGetUser(student))
                .thenReturn(false);

        assertThatThrownBy(() -> userService.getUserImage(userId))
                .isInstanceOf(NotEnoughAuthorityException.class)
                .hasMessage("You don't have the right retrieve this users information");
    }

    @Test
    void getUnexistingCustomerImage() {
        Long userId = FAKER.number().randomNumber();

        assertThatThrownBy(() -> userService.getUserImage(userId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User with id [%s] does not exist".formatted(userId));
    }

    @Test
    void getByCourseId() {
        int pageCount = FAKER.number().numberBetween(1, 10);
        int pageSize = FAKER.number().numberBetween(1, 10);

        User teacher = createUserByRole(Role.TEACHER);

        Course course = createCourseForTeacher(teacher);

        List<User> students = List.of(
                createUserByRole(Role.STUDENT),
                createUserByRole(Role.STUDENT)
        );

        Pageable pageable = PageRequest.of(pageCount, pageSize);

        when(userRepository.findByCoursesId(course.getId(), pageable))
                .thenReturn(new PageImpl<>(students, pageable, students.size()));

        List<UserDTO> resultStudents =
                userService.getByCourseId(course.getId(), pageable);

        assertThat(resultStudents)
                .containsExactlyElementsOf(students.stream().map(userDTOMapper).collect(Collectors.toList()));
    }

    @Test
    void getByCourseIdNot() {
        int pageCount = FAKER.number().numberBetween(1, 10);
        int pageSize = FAKER.number().numberBetween(1, 10);

        User teacher = createUserByRole(Role.TEACHER);

        Course course = createCourseForTeacher(teacher);

        List<User> students = List.of(
                createUserByRole(Role.STUDENT),
                createUserByRole(Role.STUDENT)
        );

        Pageable pageable = PageRequest.of(pageCount, pageSize);

        when(userRepository.findByCoursesIdNot(course.getId(), pageable))
                .thenReturn(new PageImpl<>(students, pageable, students.size()));

        List<UserDTO> resultStudents =
                userService.getByCourseIdNot(course.getId(), pageable);

        assertThat(resultStudents)
                .containsExactlyElementsOf(students.stream().map(userDTOMapper).collect(Collectors.toList()));
    }
}