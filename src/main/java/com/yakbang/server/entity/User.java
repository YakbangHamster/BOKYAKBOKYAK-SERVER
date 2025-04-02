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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", unique = true, nullable = false)
    private long userId;

    @Column(unique = true, nullable = false)
    private String identity;

    @Column(length = 255, nullable = false)
    private String password;

    @Column(length = 20)
    private String email;

    @Column(length = 10)
    private String username;

    @Column
    private int age;

    @Column
    private boolean sex;

    @Column
    private double height;

    @Column
    private double weight;

    @Column(length = 20)
    private String disease;

    public static User create(String identity, String password, String email) {
        return User.builder()
                .identity(identity)
                .password(password)
                .email(email)
                .username(null)
                .age(0)
                .sex(false)
                .height(0.0)
                .weight(0.0)
                .disease(null)
                .build();
    }

    public static User create(String identity, String password, String email, String username, int age, boolean sex, double height, double weight, String disease) {
        return User.builder()
                .identity(identity)
                .password(password)
                .email(email)
                .username(username)
                .age(age)
                .sex(sex)
                .height(height)
                .weight(weight)
                .disease(disease)
                .build();
    }
}
