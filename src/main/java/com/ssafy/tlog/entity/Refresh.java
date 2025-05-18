package com.ssafy.tlog.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Refresh {
    @Id
    @Column(nullable = false)
    private int userId;

    @Column(nullable = false)
    private String refresh;

    @Column(nullable = false)
    private LocalDateTime expiryDate;
}
