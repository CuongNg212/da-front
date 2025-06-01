package jwd.practice.userservice.repository;

import jwd.practice.userservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);
    Optional<User> findByUsernameOrEmail(String username, String email);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);

    @Query(value = "SELECT COUNT(*) AS total_users " +
            "FROM authentication.users u " +
            "WHERE DATE(u.created_at) = :date",
            nativeQuery = true)
    BigDecimal getNumberOfUsersCreatedOn(@Param("date") String date);

}
