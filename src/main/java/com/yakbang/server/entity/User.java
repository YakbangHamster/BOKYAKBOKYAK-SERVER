package com.yakbang.server.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "USER")
public class User {
    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private String userId;

    @Column(length = 10, nullable = false)
    private String password;

    @Column(length = 5, nullable = false)
    private String name;

    public static User create(String userId, String password, String name) {
        return User.builder()
                .userId(userId)
                .password(password)
                .name(name)
                .build();
    }
}
