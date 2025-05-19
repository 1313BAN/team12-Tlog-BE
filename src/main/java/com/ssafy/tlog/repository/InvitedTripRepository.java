package com.ssafy.tlog.repository;

import com.ssafy.tlog.entity.InvitedTrip;
import com.ssafy.tlog.entity.InvitedTripId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvitedTripRepository extends JpaRepository<InvitedTrip, InvitedTripId> {

}
