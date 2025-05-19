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
public class AiStory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int AiStoryId;

    private int tripId;
    private int userId;
    private String record;
}
