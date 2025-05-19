package com.ssafy.tlog.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@IdClass(InvitedTripId.class)
public class InvitedTrip {
    @Id
    private int tripId;

    @Id
    private int userId;
}
