package com.yakbang.server.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "USER_CONDITION")
public class UserCondition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "condition_id", unique = true, nullable = false)
    private long conditionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;

    @Column(name = "condition_text", length = 100, nullable = false)
    private String conditionText;

    @Column(length = 15, nullable = false)
    private String date;

    public static UserCondition create(User user, String conditionText, String date) {
        return UserCondition.builder()
                .user(user)
                .conditionText(conditionText)
                .date(date)
                .build();
    }
}
