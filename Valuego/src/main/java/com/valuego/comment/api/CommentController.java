package com.valuego.comment.api;

import com.valuego.comment.api.dto.request.CommentCreateReqDto;
import com.valuego.comment.api.dto.response.CommentInfoResDto;
import com.valuego.comment.api.dto.response.CommentListResDto;
import com.valuego.comment.service.CommentService;
import com.valuego.global.common.code.SuccessCode;
import com.valuego.global.common.template.ApiResTemplate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
@Tag(name = "Comment API", description = "장소 댓글 API")
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "댓글 전체 조회", description = "로그인한 사용자가 상세 일정에 달린 댓글 리스트를 조회합니다.")
    @GetMapping
    public ApiResTemplate<CommentListResDto> getComments(Principal principal,
                                                         @RequestParam Long travelPlaceId,
                                                         @CookieValue(value = "guestAccessToken", required = false) String guestToken) {
        CommentListResDto commentListResDto = commentService.getComments(principal, travelPlaceId, guestToken);
        return ApiResTemplate.successResponse(SuccessCode.GET_SUCCESS, commentListResDto);
    }

    @Operation(summary = "댓글 생성", description = "로그인한 사용자가 상세 일정에 댓글을 답니다.")
    @PostMapping
    public ApiResTemplate<CommentInfoResDto> createComment(Principal principal,
                                                           @RequestParam Long travelPlaceId,
                                                           @Valid @RequestBody CommentCreateReqDto commentCreateReqDto,
                                                           @CookieValue(value = "guestAccessToken", required = false) String guestToken) {
        CommentInfoResDto commentInfoResDto = commentService.createComment(principal, travelPlaceId, commentCreateReqDto, guestToken);
        return ApiResTemplate.successResponse(SuccessCode.CREATE_SUCCESS, commentInfoResDto);
    }
}
