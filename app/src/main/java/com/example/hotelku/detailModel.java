package com.example.hotelku;

public class detailModel {
    String noRoom;
    String classType;
    String availability;

    detailModel(String noRoom,
             String classType,
             String availability)
    {
        this.noRoom = noRoom;
        this.classType = classType;
        this.availability = availability;
    }
}
