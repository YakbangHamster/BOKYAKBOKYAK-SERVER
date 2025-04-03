package com.yakbang.server.composite_key;

import com.yakbang.server.entity.Medicine;
import com.yakbang.server.entity.User;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class MedicationKey implements Serializable {
    private User user;
    private Medicine medicine;
}
