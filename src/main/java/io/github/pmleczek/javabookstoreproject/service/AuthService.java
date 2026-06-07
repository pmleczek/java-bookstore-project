package io.github.pmleczek.javabookstoreproject.service;

import io.github.pmleczek.javabookstoreproject.dto.AuthRequest;
import io.github.pmleczek.javabookstoreproject.dto.AuthResponse;
import io.github.pmleczek.javabookstoreproject.dto.RegisterRequest;
import io.github.pmleczek.javabookstoreproject.entity.User;
import io.github.pmleczek.javabookstoreproject.lib.UserRole;
import io.github.pmleczek.javabookstoreproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService implements UserDetailsService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	private final AuthenticationManager authenticationManager;

	public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService,
			@Lazy AuthenticationManager authenticationManager) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtService = jwtService;
		this.authenticationManager = authenticationManager;
	}

	@NullMarked
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return userRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found"));
	}

	public AuthResponse register(RegisterRequest request) {
		User user = User.builder().username(request.username()).password(passwordEncoder.encode(request.password()))
				.role(UserRole.USER).build();
		userRepository.save(user);
		return new AuthResponse(jwtService.getToken(user));
	}

	public AuthResponse authenticate(AuthRequest req) {
		authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(req.login(), req.password()));
		User user = (User) loadUserByUsername(req.login());
		return new AuthResponse(jwtService.getToken(user));
	}
}
