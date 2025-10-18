package com.app.inventory.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "organizations",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_organization_email", columnNames = "email")
        })
public class Organization extends BaseEntity {

    @Id
    @GeneratedValue(generator = "organization_seq_gen", strategy = GenerationType.IDENTITY)
    @SequenceGenerator(name = "organization_seq_gen", sequenceName = "organization_seq", initialValue = 1)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "email")
    private String email;
}
