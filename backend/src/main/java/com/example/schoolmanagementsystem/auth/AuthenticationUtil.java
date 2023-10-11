package com.example.schoolmanagementsystem.auth;

import com.example.schoolmanagementsystem.user.Role;
import com.example.schoolmanagementsystem.user.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationUtil {
    public User getRequestUser(){
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public boolean isUserInteractingWithItself(User user) {
        return this.getRequestUser().equals(user);
    }

    public boolean isAdminInteractingWithItself(User user) {
        return this.getRequestUser().equals(user) && user.getRole().equals(Role.ADMIN);
    }

    public boolean isUserPermittedToInteractWith(Role roleToCheck) {
        Role requestRole = this.getRequestUser().getRole();

        if ((requestRole.equals(Role.ADMIN) && roleToCheck.equals(Role.TEACHER))||
                (requestRole.equals(Role.ADMIN) && roleToCheck.equals(Role.STUDENT))) {
            return true;
        }

        if ((requestRole.equals(Role.TEACHER) && roleToCheck.equals(Role.STUDENT))) {
            return true;
        }

        return false;
    }

    public boolean isUserStudent() {
        return this.getRequestUser().getRole().equals(Role.STUDENT);
    }

    public boolean isUserAdmin() {
        return this.getRequestUser().getRole().equals(Role.ADMIN);
    }
}
