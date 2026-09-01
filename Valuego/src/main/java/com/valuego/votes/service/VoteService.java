package com.valuego.votes.service;

import com.valuego.global.common.exception.EntityFinderException;
import com.valuego.global.common.exception.ValidMemberException;
import com.valuego.groups.entity.Group;
import com.valuego.travel.entity.TravelPlace;
import com.valuego.users.entity.User;
import com.valuego.votes.api.dto.request.VoteReqDto;
import com.valuego.votes.api.dto.response.VoteResDto;
import com.valuego.votes.entity.Vote;
import com.valuego.votes.entity.VoteStatus;
import com.valuego.votes.entity.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VoteService {

    private final VoteRepository voteRepository;
    private final ValidMemberException validMemberException;
    private final EntityFinderException entityFinderException;

    // 조회
    public VoteResDto getTravelVote(Principal principal, Long travelPlaceId, String guestToken) {
        TravelPlace travelPlace = entityFinderException.getTravelPlaceById(travelPlaceId);
        Group group = travelPlace.getGroup();
        validMemberException.validateGroupMember(principal, guestToken, group);

        long likeCount = voteRepository.countByTravelPlaceIdAndVoteStatus(travelPlaceId, VoteStatus.LIKE);
        long dislikeCount = voteRepository.countByTravelPlaceIdAndVoteStatus(travelPlaceId, VoteStatus.DISLIKE);
        int totalGroupMemberCount = group.getGroupMembers().size();

        VoteStatus myStatus = null;
        if (principal != null) {
            try {
                User user = entityFinderException.getUserFromPrincipal(principal);
                myStatus = voteRepository.findByUserIdAndTravelPlaceId(user.getId(), travelPlaceId)
                        .map(Vote::getVoteStatus)
                        .orElse(null);
            } catch (Exception e) {
                myStatus = null;
            }
        }

        return VoteResDto.of(likeCount, dislikeCount, totalGroupMemberCount, myStatus);
    }

    // 토글 생성
    @Transactional
    public VoteResDto toggleVote(Principal principal, Long travelPlaceId, VoteReqDto voteReqDto, String guestToken) {
        if (principal == null) {
            throw new IllegalArgumentException("로그인한 사용자만 투표할 수 있습니다.");
        }

        TravelPlace travelPlace = entityFinderException.getTravelPlaceById(travelPlaceId);
        Group group = travelPlace.getGroup();
        validMemberException.validateGroupMember(principal, guestToken, group);
        User user = entityFinderException.getUserFromPrincipal(principal);

        try {
            processToggleVote(user, travelPlace, voteReqDto);
        } catch (DataIntegrityViolationException e) {
            processToggleVote(user, travelPlace, voteReqDto);
        }

        return getTravelVote(principal, travelPlaceId, guestToken);
    }

    private void processToggleVote(User user, TravelPlace travelPlace, VoteReqDto voteReqDto) {
        Optional<Vote> existingVote = voteRepository.findByUserIdAndTravelPlaceId(user.getId(), travelPlace.getId());

        if (existingVote.isPresent()) {
            Vote vote = existingVote.get();
            if (vote.getVoteStatus() == voteReqDto.voteStatus()) {
                voteRepository.delete(vote);
            } else {
                vote.updateStatus(voteReqDto.voteStatus());
            }
        } else {
            Vote newVote = Vote.builder()
                    .user(user)
                    .travelPlace(travelPlace)
                    .voteStatus(voteReqDto.voteStatus())
                    .build();
            voteRepository.save(newVote);
            voteRepository.flush();
        }
    }
}
