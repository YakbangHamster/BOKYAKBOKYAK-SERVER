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
    @Column(name = "user_id", length = 20, nullable = false)
    private String userId;

    @Column(length = 15, nullable = false)
    private String password;

    @Column(length = 20, nullable = false)
    private String email;

    @Column(length = 10, nullable = false)
    private String name;

    @Column(nullable = false)
    private int age;

    @Column(nullable = false)
    private boolean sex;

    @Column(nullable = false)
    private double height;

    @Column(nullable = false)
    private double weight;

    @Column(length = 20)
    private String disease;

    public static User create(String userId, String password, String email, String name, int age, boolean sex, double height, double weight, String disease) {
        return User.builder()
                .userId(userId)
                .password(password)
                .email(email)
                .name(name)
                .age(age)
                .sex(sex)
                .height(height)
                .weight(weight)
                .disease(disease)
                .build();
    }
}
