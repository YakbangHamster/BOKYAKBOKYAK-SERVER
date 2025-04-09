package com.yakbang.server.entity;

import com.yakbang.server.composite_key.AlarmKey;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "user_id", referencedColumnName = "user_id"),
            @JoinColumn(name = "medicine_id", referencedColumnName = "medicine_id")
    })
    private Medication medication;

    @Column
    private List<String> timeList;

    public static Alarm create(User user, Medicine medicine, Medication medication, List<String> timeList) {
        return Alarm.builder()
                .user(user)
                .medicine(medicine)
                .medication(medication)
                .timeList(timeList)
                .build();
    }
}
