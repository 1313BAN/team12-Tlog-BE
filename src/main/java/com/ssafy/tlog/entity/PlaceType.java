package com.ssafy.tlog.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class PlaceType {
    @Id
    private int placeTypeId;
    private String placeTypeName;
}
