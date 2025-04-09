package com.yakbang.server.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ALARM")
public class Alarm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alarm_id")
    private Long alarmId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "user_id", referencedColumnName = "user_id"),
            @JoinColumn(name = "medicine_id", referencedColumnName = "medicine_id")
    })
    private Medication medication;

    @ElementCollection
    @CollectionTable(
            name = "alarm_time",
            joinColumns = @JoinColumn(name = "alarm_id")
    )
    @Column(name = "time_list")
    private List<String> timeList;

    public static Alarm create(Medication medication, List<String> timeList) {
        return Alarm.builder()
                .medication(medication)
                .timeList(timeList)
                .build();
    }
}
