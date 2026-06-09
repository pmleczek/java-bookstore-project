package io.github.pmleczek.javabookstoreproject.service;

import io.github.pmleczek.javabookstoreproject.dto.AuthRequest;
import io.github.pmleczek.javabookstoreproject.dto.AuthResponse;
import io.github.pmleczek.javabookstoreproject.dto.RegisterRequest;
import io.github.pmleczek.javabookstoreproject.entity.User;
import io.github.pmleczek.javabookstoreproject.lib.UserRole;
import io.github.pmleczek.javabookstoreproject.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    @Test
    void loadUserByUsername_whenFound_returnsUser() {
        User user = User.builder().username("alice").password("pw").role(UserRole.USER).build();
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));

        assertThat(authService.loadUserByUsername("alice")).isEqualTo(user);
    }

    @Test
    void loadUserByUsername_whenNotFound_throwsUsernameNotFoundException() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.loadUserByUsername("unknown"))
                .isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    void register_encodesPasswordSavesUserAndReturnsToken() {
        RegisterRequest request = new RegisterRequest("alice", "secret");
        when(passwordEncoder.encode("secret")).thenReturn("encoded");
        when(jwtService.getToken(any())).thenReturn("token");

        AuthResponse response = authService.register(request);

        verify(userRepository).save(argThat(u ->
                u.getPassword().equals("encoded") && u.getRole() == UserRole.USER));
        assertThat(response.token()).isEqualTo("token");
    }

    @Test
    void authenticate_delegatesToManagerAndReturnsToken() {
        AuthRequest request = new AuthRequest("alice", "secret");
        User user = User.builder().username("alice").password("encoded").role(UserRole.USER).build();
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(jwtService.getToken(user)).thenReturn("token");

        AuthResponse response = authService.authenticate(request);

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        assertThat(response.token()).isEqualTo("token");
    }
}
