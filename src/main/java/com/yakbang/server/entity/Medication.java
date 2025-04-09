package com.yakbang.server.entity;

import com.yakbang.server.composite_key.MedicationKey;
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

    @Column(name = "start_date", length = 15, nullable = false)
    private String startDate;

    @Column(name = "end_date", length = 15, nullable = false)
    private String endDate;

    @Column
    private int number;

    @Column(name = "take_record")
    private List<String> takeRecord;

    public void setTakeRecord(String record) { takeRecord.add(record); }

    public static Medication create(User user, Medicine medicine) {
        List<String> takeRecord = new ArrayList<>();

        return Medication.builder()
                .user(user)
                .medicine(medicine)
                .takeRecord(takeRecord)
                .build();
    }
}
