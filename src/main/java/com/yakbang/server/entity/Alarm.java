package com.yakbang.server.entity;

import com.yakbang.server.composite_key.AlarmKey;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Setter
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

    @Column(name = "start_date", length = 15, nullable = false)
    private String startDate;

    @Column(name = "end_date", length = 15)
    private String endDate;

    public static Alarm create(User user, Medicine medicine, String time, List<Boolean> schedule, String startDate, String endDate) {
        return Alarm.builder()
                .user(user)
                .medicine(medicine)
                .time(time)
                .schedule(schedule)
                .startDate(startDate)
                .endDate(endDate)
                .build();
    }
}
