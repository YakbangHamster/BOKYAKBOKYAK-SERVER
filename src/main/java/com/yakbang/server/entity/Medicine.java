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
@Table(name = "MEDICINE")
public class Medicine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "medicine_id", unique = true, nullable = false)
    private long medicineId;

    @Column(unique = true, nullable = false)
    private String serial;

    @Column(length = 40, nullable = false)
    private String name;

    @Column(length = 100, nullable = false)
    private String image;

    @Column(nullable = false)
    private String efficacy;

    @Column(length = 3000, nullable = false)
    private String howToTake;

    @OneToMany(mappedBy = "medicine")
    private List<Medication> medications;

    public static Medicine create(String serial, String name, String image, String efficacy, String howToTake) {
        return Medicine.builder()
                .serial(serial)
                .name(name)
                .image(image)
                .efficacy(efficacy)
                .howToTake(howToTake)
                .build();
    }
}
