package com.yakbang.server.composite_key;

import com.yakbang.server.entity.Medicine;
import com.yakbang.server.entity.User;

import java.io.Serializable;

public class AlarmKey implements Serializable {
    private User user;
    private Medicine medicine;
}
