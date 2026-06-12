package io.github.pmleczek.javabookstoreproject.filter;

import io.github.pmleczek.javabookstoreproject.entity.User;
import io.github.pmleczek.javabookstoreproject.lib.UserRole;
import io.github.pmleczek.javabookstoreproject.service.JwtService;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthFilterTest {

    @Mock private JwtService jwtService;
    @Mock private UserDetailsService userDetailsService;
    @InjectMocks private JwtAuthFilter filter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private FilterChain chain;

    @BeforeEach
    void setup() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        chain = mock(FilterChain.class);
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void teardown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void noAuthorizationHeader_continuesChainWithoutAuth() throws Exception {
        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void noBearerPrefix_continuesChainWithoutAuth() throws Exception {
        request.addHeader("Authorization", "Basic dXNlcjpwYXNz");

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void validToken_setsAuthenticationInContext() throws Exception {
        User user = User.builder().username("alice").password("pw").role(UserRole.USER).build();
        request.addHeader("Authorization", "Bearer valid.token");
        when(jwtService.getSubject("valid.token")).thenReturn("alice");
        when(userDetailsService.loadUserByUsername("alice")).thenReturn(user);
        when(jwtService.isValid("valid.token", user)).thenReturn(true);

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).isEqualTo(user);
    }

    @Test
    void tokenForUnknownUser_continuesChainWithoutAuth() throws Exception {
        request.addHeader("Authorization", "Bearer ghost.token");
        when(jwtService.getSubject("ghost.token")).thenReturn("ghost");
        when(userDetailsService.loadUserByUsername("ghost"))
                .thenThrow(new UsernameNotFoundException("not found"));

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void tokenFailsValidation_continuesChainWithoutAuth() throws Exception {
        User user = User.builder().username("alice").password("pw").role(UserRole.USER).build();
        request.addHeader("Authorization", "Bearer expired.token");
        when(jwtService.getSubject("expired.token")).thenReturn("alice");
        when(userDetailsService.loadUserByUsername("alice")).thenReturn(user);
        when(jwtService.isValid("expired.token", user)).thenReturn(false);

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }
}
