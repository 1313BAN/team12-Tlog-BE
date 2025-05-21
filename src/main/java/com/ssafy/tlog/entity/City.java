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
public class City {
    @Id
    private int cityId;
    private String cityEn;
    private String cityKo;
}
