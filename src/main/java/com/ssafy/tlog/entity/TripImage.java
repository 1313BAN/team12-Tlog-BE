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
public class TripImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int imageId;

    private int tripId;
    private int userId;
    private int day;
    private String imageUrl;      // 이미지 파일 URL
    private String originalName;  // 원본 파일명
    private long fileSize;        // 파일 크기
}