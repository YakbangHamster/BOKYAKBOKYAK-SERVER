package com.yakbang.server.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
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

    @Column(name = "emoji_code", length = 3, nullable = false)
    private String emojiCode;

    @Column(length = 15, nullable = false)
    private LocalDate date;

    public static UserCondition create(User user, String emojiCode, LocalDate date) {
        return UserCondition.builder()
                .user(user)
                .emojiCode(emojiCode)
                .date(date)
                .build();
    }
}
