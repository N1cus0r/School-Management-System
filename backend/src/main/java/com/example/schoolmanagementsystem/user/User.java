package com.example.schoolmanagementsystem.user;

import com.example.schoolmanagementsystem.course.Course;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "user_email_unique",
                        columnNames = "email"
                ),
                @UniqueConstraint(
                        name = "user_mobile_phone_unique",
                        columnNames = "mobilePhone"
                ),
                @UniqueConstraint(
                        name = "user_profile_image_id_unique",
                        columnNames = "profileImageId"
                ),
        }
)
public class User implements UserDetails {
    @Id
    @GeneratedValue
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Column(unique = true)
    private String mobilePhone;

    private LocalDate dateOfBirth;

    @Column(nullable = false)
    private LocalDate registrationDate;

    @Column(unique = true)
    private String profileImageId;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "student_course",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    private Set<Course> courses = new HashSet<>();

    @PrePersist
    public void prePersist() {
        this.registrationDate = LocalDate.now();
        this.courses = new HashSet<>();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(this.role.name()));
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && role == user.role && Objects.equals(email, user.email) && Objects.equals(password, user.password) && Objects.equals(fullName, user.fullName) && gender == user.gender && Objects.equals(mobilePhone, user.mobilePhone) && Objects.equals(dateOfBirth, user.dateOfBirth) && Objects.equals(registrationDate, user.registrationDate) && Objects.equals(profileImageId, user.profileImageId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, role, email, password, fullName, gender, mobilePhone, dateOfBirth, registrationDate, profileImageId);
    }
}
