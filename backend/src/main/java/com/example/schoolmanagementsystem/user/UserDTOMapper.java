package com.example.schoolmanagementsystem.user;

import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class UserDTOMapper implements Function<User, UserDTO> {
    @Override
    public UserDTO apply(User user) {
        return new UserDTO(
                user.getId(),
                user.getRole().name(),
                user.getEmail(),
                user.getFullName(),
                user.getGender(),
                user.getMobilePhone(),
                user.getDateOfBirth(),
                user.getRegistrationDate(),
                user.getProfileImageId()
        );
    }
}
