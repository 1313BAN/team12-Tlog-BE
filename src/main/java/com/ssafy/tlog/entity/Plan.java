package com.ssafy.tlog.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Plan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int planId;

    private int cityId;
    private int tripId;
    private int placeId;
    private int day;
    private int order;
    private double latitude;
    private double longitude;
    private String memo;

}
