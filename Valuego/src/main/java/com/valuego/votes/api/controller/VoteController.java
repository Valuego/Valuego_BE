package com.valuego.votes.api.controller;

import com.valuego.global.common.code.SuccessCode;
import com.valuego.global.common.template.ApiResTemplate;
import com.valuego.votes.api.dto.request.VoteReqDto;
import com.valuego.votes.api.dto.response.VoteResDto;
import com.valuego.votes.service.VoteService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/places/vote")
@RequiredArgsConstructor
public class VoteController {

    private final VoteService voteService;

    @Operation(summary = "장소 반응(좋아요/별로예요) 통계 조회", description = "장소의 좋아요/별로예요 개수 및 투표율을 조회합니다.")
    @GetMapping
    public ApiResTemplate<VoteResDto> getTravelVote(Principal principal,
                                                    @RequestParam Long travelPlaceId,
                                                    @CookieValue(value = "guestAccessToken", required = false) String guestToken) {
        VoteResDto voteResDto = voteService.getTravelVote(principal, travelPlaceId, guestToken);

        return ApiResTemplate.successResponse(SuccessCode.GET_SUCCESS, voteResDto);
    }

    @Operation(summary = "장소 투표(좋아요/별로예요) 토글", description = "로그인한 사용자가 좋아요/별로예요 토글로 투표합니다.")
    @PostMapping
    public ApiResTemplate<VoteResDto> toggleVote(Principal principal,
                                                 @RequestParam Long travelPlaceId,
                                                 @Valid @RequestBody VoteReqDto VoteReqDto,
                                                 @CookieValue(value = "guestAccessToken", required = false) String guestToken) {
        VoteResDto voteResDto = voteService.toggleVote(principal, travelPlaceId, VoteReqDto, guestToken);

        return ApiResTemplate.successResponse(SuccessCode.CREATE_SUCCESS, voteResDto);
    }
}
