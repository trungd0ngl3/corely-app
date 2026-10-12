package com.corely.corely_backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "stores")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Store extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String slug;

    private String logoUrl;

    private String bannerUrl;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String contactPhone;

    private String contactEmail;

    private String address;

    @Builder.Default
    private Boolean isActive = true;

    @Builder.Default
    private Boolean isVerified = true;

    @Builder.Default
    private Double rating = 5.0;

    @OneToMany(mappedBy = "store")
    private List<Product> products;
}
