package com.ankap.platform.marketplace.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "users")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true, length = 200)
  private String email;

  @Column(name = "password_hash", nullable = false, length = 200)
  private String passwordHash;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 40)
  private UserRole role;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt = Instant.now();

  protected User() {}

  public User(String email, String passwordHash, UserRole role) {
    this.email = email;
    this.passwordHash = passwordHash;
    this.role = role;
  }

  public Long getId() { return id; }
  public String getEmail() { return email; }
  public String getPasswordHash() { return passwordHash; }
  public UserRole getRole() { return role; }
}