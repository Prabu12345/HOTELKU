package com.example.hotelku.module;

import java.util.Date;

public class userModel {
    public String username;
    public String email;
    public String address;
    public Long birthdate;
    public String gender;
    public String avatarUrl;

    public userModel() {

    }

    public userModel(String username, String email, String address, Long birthdate, String gender, String avatarUrl) {
        this.username = username;
        this.email = email;
        this.address = address;
        this.birthdate = birthdate;
        this.gender = gender;
        this.avatarUrl = avatarUrl;
    }
}
