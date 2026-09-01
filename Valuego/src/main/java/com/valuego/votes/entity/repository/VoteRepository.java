package com.valuego.votes.entity.repository;

import com.valuego.votes.entity.Vote;
import com.valuego.votes.entity.VoteStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
    Optional<Vote> findByUserIdAndTravelPlaceId(Long userId, Long travelPlaceId);

    long countByTravelPlaceIdAndVoteStatus(Long travelPlaceId, VoteStatus voteStatus);
}
