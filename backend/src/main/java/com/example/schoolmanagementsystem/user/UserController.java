package com.example.schoolmanagementsystem.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("{email}")
    public UserDTO getUserByEmail(
            @PathVariable("email") String email
    ) {
        return userService.getUserByEmail(email);
    }

    @GetMapping("students")
    public List<UserDTO> searchStudentsByFullName(
            @RequestParam(defaultValue = "") String fullNameSearch,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        return userService.getAllStudents(fullNameSearch, page, size);
    }

    @GetMapping("teachers")
    public List<UserDTO> searchTeachersByFullName(
            @RequestParam(defaultValue = "") String fullNameSearch,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        return userService.getAllTeachers(fullNameSearch, page, size);
    }

    @PostMapping("register")
    public void registerUser(
            @Valid @RequestBody UserRegistrationRequest request
    ) {
        userService.registerUser(request);
    }

    @PutMapping("{id}")
    public void updateUser(
            @PathVariable("id") Long userId,
            @Valid @RequestBody UpdateUserRequest request
    ) {
        userService.updateUser(userId, request);
    }

    @DeleteMapping("{id}")
    public void deleteUser(@PathVariable("id") Long userId) {
        userService.deleteUserById(userId);
    }

    @PutMapping(
            value = "{id}/profile-image",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public void uploadUserProfileImage(
            @PathVariable("id") Long userId,
            @RequestParam("file") MultipartFile file
    ) {
        userService.uploadUserProfileImage(userId, file);
    }

    @GetMapping(
            value = "{id}/profile-image",
            produces = MediaType.IMAGE_JPEG_VALUE
    )
    public byte[] getUserProfileImage(
            @PathVariable("id") Long userId
    ) {
        return userService.getUserImage(userId);
    }
}


