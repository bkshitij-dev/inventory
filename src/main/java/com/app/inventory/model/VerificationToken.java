package com.app.inventory.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "verification_tokens",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_verification_token_token", columnNames = "token")
        })
public class VerificationToken extends BaseEntity {

    @Id
    @GeneratedValue(generator = "verification_token_seq_gen", strategy = GenerationType.IDENTITY)
    @SequenceGenerator(name = "verification_token_seq_gen", sequenceName = "verification_token_seq", initialValue = 1)
    private Long id;

    @Column(name = "token", nullable = false)
    private String token;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate;

    @Column(name = "active")
    @Builder.Default
    private boolean active = true;

    public boolean isExpired() {
        return expiryDate.isBefore(LocalDateTime.now());
    }
}
