package com.app.inventory.repository;

import com.app.inventory.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query(value = "SELECT u.* FROM users u WHERE u.username = ?1 OR u.email = ?2", nativeQuery = true)
    Optional<User> findByUsernameOrEmail(String username, String email);

}
