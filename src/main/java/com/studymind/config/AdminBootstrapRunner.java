package com.studymind.config;

import com.studymind.model.enums.UserRole;
import com.studymind.repository.UserRepository;
import com.studymind.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("!test")
public class AdminBootstrapRunner implements ApplicationRunner {

    private final UserRepository userRepository;
    private final UserService userService;

    @Value("${app.bootstrap-admin.enabled:true}")
    private boolean enabled;

    @Value("${app.bootstrap-admin.email:admin@studymind.com}")
    private String email;

    @Value("${app.bootstrap-admin.password:Admin@123456}")
    private String password;

    @Value("${app.bootstrap-admin.full-name:System Administrator}")
    private String fullName;

    public AdminBootstrapRunner(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!enabled || userRepository.existsByRole(UserRole.ADMIN)) {
            return;
        }

        userService.createUser(email, password, fullName, UserRole.ADMIN, null, null, null);
        log.info("Bootstrap admin account created: {}", email);
    }
}
