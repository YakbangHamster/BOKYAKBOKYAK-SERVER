package com.yakbang.server.entity;

import com.yakbang.server.composite_key.MedicationKey;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@IdClass(MedicationKey.class)
@Table(name = "MEDICATION")
public class Medication {
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medicine_id", referencedColumnName = "medicine_id")
    private Medicine medicine;

    @Column
    private List<Boolean> schedule;

    @Column(name = "start_date", length = 15)
    private LocalDate startDate;

    @Column(name = "end_date", length = 15)
    private LocalDate endDate;

    @Column
    private int number;

    @Column(name = "take_record")
    private List<LocalDate> takeRecord;

    @OneToOne(mappedBy = "medication", cascade = CascadeType.ALL, orphanRemoval = true)
    private Alarm alarm;

    public void setTakeRecord(LocalDate record) { takeRecord.add(record); }

    public static Medication create(User user, Medicine medicine) {
        List<Boolean> schedule = new ArrayList<>();
        List<LocalDate> takeRecord = new ArrayList<>();

        return Medication.builder()
                .user(user)
                .medicine(medicine)
                .schedule(schedule)
                .startDate(null)
                .endDate(null)
                .number(0)
                .takeRecord(takeRecord)
                .build();
    }
}
