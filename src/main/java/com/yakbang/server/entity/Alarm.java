package com.yakbang.server.entity;

import com.yakbang.server.composite_key.AlarmKey;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@IdClass(AlarmKey.class)
@Table(name = "ALARM")
public class Alarm {
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medicine_id", referencedColumnName = "medicine_id")
    private Medicine medicine;

    @Column(length = 10, nullable = false)
    private String time;

    @Column(nullable = false)
    private List<Boolean> schedule;

    @Column(length = 15, nullable = false)
    private String start_date;

    @Column(length = 15)
    private String end_date;

    public static Alarm create(User user, Medicine medicine, String time, List<Boolean> schedule, String start_date, String end_date) {
        return Alarm.builder()
                .user(user)
                .medicine(medicine)
                .time(time)
                .schedule(schedule)
                .start_date(start_date)
                .end_date(end_date)
                .build();
    }
}
