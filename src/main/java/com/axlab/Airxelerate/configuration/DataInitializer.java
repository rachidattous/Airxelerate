package com.axlab.Airxelerate.configuration;

import com.axlab.Airxelerate.entity.User;
import com.axlab.Airxelerate.enums.Role;
import com.axlab.Airxelerate.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
@Log4j2
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if(userRepository.count() == 0){
            User admin = User.builder()
                    .firstName("Super")
                    .lastName("Admin")
                    .email("admin@example.com")
                    .username("adminadmin")
                    .password(passwordEncoder.encode("Admin@1234"))
                    .role(Role.ADMIN)
                    .build();

            userRepository.save(admin);

            log.info("First admin user created: {}", admin.getEmail());
        } else {
            log.info("Users already exist, skipping initial admin creation.");
        }
    }
}
