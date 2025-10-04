package com.app.inventory.model;

import com.app.inventory.enums.Role;
import jakarta.persistence.*;
import lombok.Builder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Entity
@Builder
@Table(name = "users",
        uniqueConstraints = {
        @UniqueConstraint(name = "uk_user_username", columnNames = "username"),
        @UniqueConstraint(name = "uk_user_email", columnNames = "email")
})
public class User extends BaseEntity {

    @Id
    @GeneratedValue(generator = "user_seq_gen", strategy = GenerationType.IDENTITY)
    @SequenceGenerator(name = "user_seq_gen", sequenceName = "user_seq", initialValue = 1)
    private Long id;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

}
