package com.yakbang.server.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false, length = 500)
    private String token;

    @Column(nullable = false)
    private LocalDateTime expiryDate;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    public void updateToken(String newToken, LocalDateTime expiryDate) {
        this.token = newToken;
        this.expiryDate = expiryDate;
    }

    public static RefreshToken create(String token, LocalDateTime expiryDate, User user) {
        return RefreshToken.builder()
                .token(token)
                .expiryDate(expiryDate)
                .user(user)
                .build();
    }
}
