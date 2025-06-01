package jwd.practice.userservice.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int userId;

    @Column(unique = true, columnDefinition = "VARCHAR(255) COLLATE utf8mb4_unicode_ci")
    String username;
    String password;
    @Column(unique = true)
    String email;
    String phone;
    String avatar;
    String address;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;

    boolean enabled;

    @ElementCollection
    Set<String> roles;
}
