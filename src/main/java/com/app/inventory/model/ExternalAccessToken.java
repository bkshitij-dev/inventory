package com.app.inventory.model;

import com.app.inventory.enums.TokenType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "external_access_tokens",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_external_access_token_token", columnNames = "token")
        })
public class ExternalAccessToken extends BaseEntity {

    @Id
    @GeneratedValue(generator = "external_access_token_seq_gen", strategy = GenerationType.IDENTITY)
    @SequenceGenerator(name = "external_access_token_seq_gen", sequenceName = "external_access_token_seq",
            initialValue = 1)
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

    @Enumerated(EnumType.STRING)
    @Column(name = "token_type", nullable = false)
    private TokenType tokenType;

    public boolean isExpired() {
        return expiryDate.isBefore(LocalDateTime.now());
    }
}
