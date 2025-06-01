package jwd.practice.userservice.config;


import jwd.practice.userservice.entity.Role;
import jwd.practice.userservice.entity.User;
import jwd.practice.userservice.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AutoCreateConfig {
    PasswordEncoder passwordEncoder;

    @Bean
    @ConditionalOnProperty(prefix = "spring", value = "datasource.driver-class-name", havingValue = "com.mysql.cj.jdbc.Driver")
    ApplicationRunner applicationRunner(UserRepository userRepository) {
        return args -> {
            if (userRepository.findByUsername("admin").isEmpty()) {
                var role = new HashSet<String>();
                role.add(Role.ADMIN.name());

                User user = User.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("admin"))
                        .roles(role)
                        .createdAt(LocalDateTime.now())
                        .email("admin@admin.com")
                        .build();
                userRepository.save(user);
            }
        };
    }
}
