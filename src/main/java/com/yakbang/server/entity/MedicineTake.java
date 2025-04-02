package com.yakbang.server.entity;

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
@Table(name = "MEDICINE_TAKE")
public class MedicineTake {
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medicine_id", referencedColumnName = "medicine_id")
    private Medicine medicine;

    @Column
    private List<String> take_record;

    public static MedicineTake create(User user, Medicine medicine, List<String> take_record) {
        return MedicineTake.builder()
                .user(user)
                .medicine(medicine)
                .build();
    }
}
