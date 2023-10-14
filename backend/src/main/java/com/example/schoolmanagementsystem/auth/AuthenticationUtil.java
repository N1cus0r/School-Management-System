package com.example.schoolmanagementsystem.auth;

import com.example.schoolmanagementsystem.user.Role;
import com.example.schoolmanagementsystem.user.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationUtil {
    public User getRequestUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public boolean isUserPermittedToCreateUsersWithRole(Role roleToCheck) {
        Role requestRole = this.getRequestUser().getRole();

        return (requestRole.equals(Role.ADMIN) && roleToCheck.equals(Role.TEACHER)) ||
                (requestRole.equals(Role.ADMIN) && roleToCheck.equals(Role.STUDENT)) ||
                (requestRole.equals(Role.TEACHER) && roleToCheck.equals(Role.STUDENT));
    }

    public boolean isUserPermittedToGetUser(User user) {
        User requestUser = this.getRequestUser();
        Role requestRole = requestUser.getRole();
        Role roleToCheck = user.getRole();

        return (requestRole.equals(Role.ADMIN) && user.equals(requestUser)) ||
                (requestRole.equals(Role.TEACHER) && user.equals(requestUser)) ||
                (requestRole.equals(Role.STUDENT) && user.equals(requestUser)) ||
                (requestRole.equals(Role.ADMIN) && roleToCheck.equals(Role.TEACHER)) ||
                (requestRole.equals(Role.ADMIN) && roleToCheck.equals(Role.STUDENT)) ||
                (requestRole.equals(Role.TEACHER) && roleToCheck.equals(Role.STUDENT));
    }

    public boolean isUserPermittedToUpdateUser(User user) {
        User requestUser = this.getRequestUser();
        Role requestRole = requestUser.getRole();
        Role roleToCheck = user.getRole();

        return (requestRole.equals(Role.ADMIN) && user.equals(requestUser)) ||
                (requestRole.equals(Role.ADMIN) && roleToCheck.equals(Role.TEACHER)) ||
                (requestRole.equals(Role.ADMIN) && roleToCheck.equals(Role.STUDENT)) ||
                (requestRole.equals(Role.TEACHER) && roleToCheck.equals(Role.STUDENT));
    }

    public boolean isUserPermittedToDeleteUser(User user) {
        User requestUser = this.getRequestUser();
        Role requestRole = requestUser.getRole();
        Role roleToCheck = user.getRole();

        return (requestRole.equals(Role.ADMIN) && roleToCheck.equals(Role.TEACHER)) ||
                (requestRole.equals(Role.ADMIN) && roleToCheck.equals(Role.STUDENT)) ||
                (requestRole.equals(Role.TEACHER) && roleToCheck.equals(Role.STUDENT));
    }

    public boolean isUserStudent() {
        return this.getRequestUser().getRole().equals(Role.STUDENT);
    }

    public boolean isUserAdmin() {
        return this.getRequestUser().getRole().equals(Role.ADMIN);
    }
}
