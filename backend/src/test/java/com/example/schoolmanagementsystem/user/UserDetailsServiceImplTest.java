package com.example.schoolmanagementsystem.user;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
@ActiveProfiles("dev")

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {
    private final Faker FAKER = new Faker();
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Test
    void loadUserByUsername() {
        User user =  User.builder()
                .role(Role.STUDENT)
                .email(FAKER.internet().safeEmailAddress())
                .password(FAKER.internet().password())
                .fullName(FAKER.name().fullName())
                .gender(Gender.MALE)
                .build();


        when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));

        ArgumentCaptor<String> usernameArgumentCaptor =
                ArgumentCaptor.forClass(String.class);

        userDetailsService.loadUserByUsername(user.getUsername());

        verify(userRepository)
                .findByEmail(usernameArgumentCaptor.capture());

        assertThat(usernameArgumentCaptor.getValue())
                .isEqualTo(user.getUsername());
    }

    @Test
    void loadUserByUsernameThrowsException() {
        String username = FAKER.internet().safeEmailAddress();

        assertThatThrownBy(
                () -> userDetailsService.loadUserByUsername(username))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("Username %s not found".formatted(username));


    }
}