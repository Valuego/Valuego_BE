package com.valuego.comment.service;

import com.valuego.comment.api.dto.request.CommentCreateReqDto;
import com.valuego.comment.api.dto.response.CommentInfoResDto;
import com.valuego.comment.api.dto.response.CommentListResDto;
import com.valuego.comment.entity.Comment;
import com.valuego.comment.entity.repository.CommentRepository;
import com.valuego.global.common.exception.EntityFinderException;
import com.valuego.global.common.exception.ValidMemberException;
import com.valuego.groups.entity.Group;
import com.valuego.travel.entity.TravelPlace;
import com.valuego.travel.entity.repository.TravelPlaceRepository;
import com.valuego.users.entity.User;
import com.valuego.users.entity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final TravelPlaceRepository travelPlaceRepository;
    private final UserRepository userRepository;
    private final EntityFinderException entityFinderException;
    private final ValidMemberException validMemberException;

    // 전체 조회
    public CommentListResDto getComments(Principal principal, Long travelPlaceId, String guestToken) {
        TravelPlace travelPlace = entityFinderException.getTravelPlaceById(travelPlaceId);
        Group group = travelPlace.getGroup();
        validMemberException.validateGroupMember(principal, guestToken, group);

        List<Comment> comments = commentRepository.findAllByTravelPlaceIdWithUser(travelPlaceId);

        return CommentListResDto.of(comments);

    }

    // 댓글 생성
    @Transactional
    public CommentInfoResDto createComment(Principal principal, Long travelPlaceId, CommentCreateReqDto commentCreateReqDto, String guestToken) {
        TravelPlace travelPlace = entityFinderException.getTravelPlaceById(travelPlaceId);

        Group group = travelPlace.getGroup();
        validMemberException.validateGroupMember(principal, guestToken, group);

        User user = entityFinderException.getUserFromPrincipal(principal);

        Comment comment = Comment.builder()
                .user(user)
                .travelPlace(travelPlace)
                .content(commentCreateReqDto.content())
                .build();

        Comment savedComment = commentRepository.save(comment);

        return CommentInfoResDto.from(savedComment);
    }
}
