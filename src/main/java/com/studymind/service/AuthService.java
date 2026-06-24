package com.studymind.service;

import com.studymind.dto.auth.AuthResponse;
import com.studymind.dto.auth.LoginRequest;
import com.studymind.dto.auth.RegisterStudentRequest;
import com.studymind.dto.auth.UserResponse;
import com.studymind.exception.UnauthorizedException;
import com.studymind.model.User;
import com.studymind.model.enums.UserRole;
import com.studymind.repository.UserRepository;
import com.studymind.security.JwtService;
import com.studymind.security.UserPrincipal;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(
            UserRepository userRepository,
            UserService userService,
            JwtService jwtService,
            AuthenticationManager authenticationManager
    ) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public UserResponse registerStudent(RegisterStudentRequest request) {
        User savedUser = userService.createUser(
                request.email(),
                request.password(),
                request.fullName(),
                UserRole.STUDENT,
                request.studentCode(),
                null,
                null
        );
        return UserResponse.from(savedUser);
    }

    public AuthResponse login(LoginRequest request) {
        String email = userService.normalizeEmail(request.email());

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (Boolean.FALSE.equals(user.getActive())) {
            throw new UnauthorizedException("Account is disabled. Contact administrator.");
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, request.password())
            );

            UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
            String token = jwtService.generateToken(principal);
            return AuthResponse.of(token, UserResponse.from(user));
        } catch (DisabledException ex) {
            throw new UnauthorizedException("Account is disabled. Contact administrator.");
        } catch (BadCredentialsException ex) {
            throw new UnauthorizedException("Invalid email or password");
        } catch (AuthenticationException ex) {
            throw new UnauthorizedException("Invalid email or password");
        }
    }

    public UserResponse getCurrentUser(UserPrincipal principal) {
        User user = userRepository.findByEmail(principal.getEmail())
                .orElseThrow(() -> new UnauthorizedException("User not found"));
        return UserResponse.from(user);
    }
}
