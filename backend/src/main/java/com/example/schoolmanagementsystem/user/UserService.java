package com.example.schoolmanagementsystem.user;

import com.example.schoolmanagementsystem.auth.AuthenticationUtil;
import com.example.schoolmanagementsystem.exception.NotEnoughAuthorityException;
import com.example.schoolmanagementsystem.exception.ResourceNotFoundException;
import com.example.schoolmanagementsystem.exception.RequestValidationError;
import com.example.schoolmanagementsystem.exception.UserEmailTakeException;
import com.example.schoolmanagementsystem.util.UpdateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserDTOMapper userDTOMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationUtil authenticationUtil;
    private final UpdateUtil updateUtil;

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.password}")
    private String adminPassword;

    private void createAdminUser() {
        User admin = User.builder()
                .role(Role.ADMIN)
                .email(adminEmail)
                .password(passwordEncoder.encode(adminPassword))
                .fullName("Admin")
                .gender(Gender.MALE)
                .build();

        userRepository.save(admin);
    }

    public void generateAnAdminUserIfNone() {
        if (!userRepository.existsByRole(Role.ADMIN)) {
            createAdminUser();
        }
    }

    public void registerUser(UserRegistrationRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new UserEmailTakeException("Provided email is already in use");
        }

        if (!authenticationUtil.isUserPermittedToInteractWith(request.role())) {
            throw new NotEnoughAuthorityException("You don't have the right to create users with this role");
        }

        User user = User.builder()
                .role(request.role())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .fullName(request.fullName())
                .gender(request.gender())
                .mobilePhone(request.mobilePhone())
                .dateOfBirth(request.dateOfBirth())
                .build();

        userRepository.save(user);
    }

    private User getUserByIdAndThrowIfNotFound(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User with id [%s] does not exist".formatted(userId)));
    }
    public UserDTO getUserById(Long userId) {
        User user = getUserByIdAndThrowIfNotFound(userId);

        if (!authenticationUtil.isUserPermittedToInteractWith(user.getRole()) &&
            !authenticationUtil.isUserInteractingWithItself(user)) {
            throw new NotEnoughAuthorityException("You don't have the right to retrieve users with this role");
        }

        return userDTOMapper.apply(user);
    }

    public UserDTO getUserByEmail(String email) {
        if (!authenticationUtil.isUserAdmin()) {
            throw new NotEnoughAuthorityException("You don't have the right to access this resource");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User with email [%s] does not exist".formatted(email)));

        return userDTOMapper.apply(user);
    }

    private List<UserDTO> getUsersSearchedByFullNameSortedByRole(
            Role role,
            String fullNameSearch,
            int pageCount,
            int pageSize
    ) {
        Pageable pageable = PageRequest.of(pageCount, pageSize);
        Page<User> userPage;

        if (fullNameSearch.isEmpty()) {
            userPage = userRepository.findByRole(role, pageable);
        } else {
            userPage = userRepository.findByRoleAndFullNameStartsWithIgnoreCase(role, fullNameSearch, pageable);
        }

        return userPage.getContent()
                .stream()
                .map(userDTOMapper)
                .collect(Collectors.toList());
    }

    public List<UserDTO> getAllStudents(
            String fullNameSearch,
            int pageCount,
            int pageSize
    ) {
        if (authenticationUtil.isUserStudent()) {
            throw new NotEnoughAuthorityException("You don't have the right to access this information");
        }
        return getUsersSearchedByFullNameSortedByRole(
                Role.STUDENT, fullNameSearch, pageCount, pageSize);
    }

    public List<UserDTO> getAllTeachers(String fullNameSearch, int pageCount, int pageSize) {
        if (!authenticationUtil.isUserAdmin()) {
            throw new NotEnoughAuthorityException("You don't have the right to access this information");
        }
        return getUsersSearchedByFullNameSortedByRole(
                Role.TEACHER, fullNameSearch, pageCount, pageSize);
    }

    public void updateUser(Long userId, UpdateUserRequest request) {

        User user = getUserByIdAndThrowIfNotFound(userId);

        if (!authenticationUtil.isUserPermittedToInteractWith(user.getRole()) &&
                !authenticationUtil.isAdminInteractingWithItself(user)) {
            throw new NotEnoughAuthorityException("You don't have the right to retrieve users with this role");
        }

        boolean changes = false;

        if (!updateUtil.isFieldNullOrWithoutChange(user.getEmail(), request.email())) {
            if (userRepository.existsByEmail(request.email())) {
                throw new UserEmailTakeException(
                        "Provided email is already in use"
                );
            }
            changes = true;
            user.setEmail(request.email());
        }

        if (!updateUtil.isFieldNullOrWithoutChange(user.getFullName(), request.fullName())) {
            changes = true;
            user.setFullName(request.fullName());
        }

        if (!updateUtil.isFieldNullOrWithoutChange(user.getGender(), request.gender())) {
            changes = true;
            user.setGender(request.gender());
        }

        if (!updateUtil.isFieldNullOrWithoutChange(user.getMobilePhone(), request.mobilePhone())) {
            changes = true;
            user.setMobilePhone(request.mobilePhone());
        }

        if (!updateUtil.isFieldNullOrWithoutChange(user.getDateOfBirth(), request.dateOfBirth())) {
            changes = true;
            user.setDateOfBirth(request.dateOfBirth());
        }

        if (!changes) {
            throw new RequestValidationError("No data changes found");
        }

        userRepository.save(user);
    }

    public void deleteUserById(Long userId) {
        User user = getUserByIdAndThrowIfNotFound(userId);

        if (!authenticationUtil.isUserPermittedToInteractWith(user.getRole())) {
            throw new NotEnoughAuthorityException("You don't have the right to interact with users with this role");
        }

        userRepository.delete(user);
    }


}
