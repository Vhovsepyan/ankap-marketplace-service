package com.ankap.platform.marketplace.api;

import com.ankap.platform.marketplace.app.JwtService;
import com.ankap.platform.marketplace.domain.*;
import com.ankap.platform.marketplace.infra.UserRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Validated
public class AuthController {

  private final UserRepository userRepo;
  private final PasswordEncoder encoder;
  private final JwtService jwtService;

  public AuthController(UserRepository userRepo, PasswordEncoder encoder, JwtService jwtService) {
    this.userRepo = userRepo;
    this.encoder = encoder;
    this.jwtService = jwtService;
  }

  public record RegisterRequest(
          @Email @NotBlank String email,
          @NotBlank @Size(min = 8, max = 100) String password,
          @NotNull UserRole role
  ) {}

  public record LoginRequest(
          @Email @NotBlank String email,
          @NotBlank String password
  ) {}

  public record AuthResponse(String accessToken) {}

  @PostMapping("/register")
  public ResponseEntity<AuthResponse> register(@RequestBody @Valid RegisterRequest req) {
    if (userRepo.existsByEmail(req.email())) {
      throw new IllegalArgumentException("email already registered");
    }

    User u = new User(req.email(), encoder.encode(req.password()), req.role());
    u = userRepo.save(u);

    String token = jwtService.issueAccessToken(u.getId(), u.getRole());
    return ResponseEntity.ok(new AuthResponse(token));
  }

  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequest req) {
    User u = userRepo.findByEmail(req.email())
            .orElseThrow(() -> new IllegalArgumentException("invalid credentials"));

    if (!encoder.matches(req.password(), u.getPasswordHash())) {
      throw new IllegalArgumentException("invalid credentials");
    }

    String token = jwtService.issueAccessToken(u.getId(), u.getRole());
    return ResponseEntity.ok(new AuthResponse(token));
  }
}