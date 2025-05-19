package com.ssafy.tlog.repository;

import com.ssafy.tlog.entity.TripParticipant;
import com.ssafy.tlog.entity.TripParticipantId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TripParticipantRepository extends JpaRepository<TripParticipant, TripParticipantId> {

}
