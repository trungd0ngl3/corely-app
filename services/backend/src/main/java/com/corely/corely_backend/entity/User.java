package com.corely.corely_backend.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @Column(unique = true, nullable = false)
    String email;

    String password;

    String fullName;
    String phone;
    
    @Column(length = 500)
    String avatarUrl;   

    @Column(length = 200)
    String avatarPublicId;

    LocalDate dateOfBirth;

    // OAuth2 fields
    String provider; // "local", "google", "facebook"
    String providerId;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_name"))
    Set<Role> roles;

    @Builder.Default
    @Column(nullable = false)
    Boolean emailVerified = false;

    @Builder.Default
    @Column(nullable = false)
    Boolean isActive = false;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    List<Address> addresses;
}
