package com.valuego.games.api;

import com.valuego.games.api.dto.request.GameCreateReqDto;
import com.valuego.games.api.dto.response.GameMemberListResDto;
import com.valuego.games.api.dto.response.GameResultResDto;
import com.valuego.games.quiz.dto.response.QuizOptionListResDto;
import com.valuego.games.quiz.dto.request.QuizAnswerReqDto;
import com.valuego.games.quiz.dto.response.QuizAnswerResDto;
import com.valuego.games.service.GameService;
import com.valuego.games.service.LocalQuizService;
import com.valuego.global.common.code.SuccessCode;
import com.valuego.global.common.template.ApiResTemplate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/games")
@Tag(name = "Game API", description = "게임 API")
public class GameController {

    private final GameService gameService;
    private final LocalQuizService localQuizService;

    @Operation(summary = "사다리 타기", description = "그룹 id로 판별하여 벌칙 작성 후 사다리 타기")
    @PostMapping("/ladder")
    public GameResultResDto createLadder(Principal principal,
                                         @RequestParam Long groupId,
                                         @Valid @RequestBody GameCreateReqDto gameCreateReqDto,
                                         @CookieValue(value = "guestAccessToken", required = false) String guestToken) {
        return gameService.createLadder(principal, groupId, gameCreateReqDto, guestToken);
    }

    @Operation(summary = "룰렛", description = "그룹 id로 판별하여 벌칙 생성 후 룰렛 돌리기")
    @PostMapping("/roulette")
    public GameResultResDto createRoulette(Principal principal,
                                           @RequestParam Long groupId,
                                           @Valid @RequestBody GameCreateReqDto gameCreateReqDto,
                                           @CookieValue(value = "guestAccessToken", required = false) String guestToken) {
        return gameService.createRoulette(principal, groupId, gameCreateReqDto, guestToken);
    }

    @Operation(summary = "멤버 리스트 조회", description = "게임을 하기 위한 그룹별 멤버 리스트를 조회합니다.")
    @PostMapping("/members")
    public List<GameMemberListResDto> getGameMembers(Principal principal,
                                                     @RequestParam Long groupId,
                                                     @CookieValue(value = "guestAccessToken", required = false) String guestToken) {
        return gameService.getGameMembers(principal, groupId, guestToken);
    }

    @Operation(summary = "로컬 퀴즈 생성", description = "GEMINI API로 퀴즈 생성 요청을 보냅니다. 약 8초 소요됩니다.")
    @PostMapping("/quiz")
    public ApiResTemplate<QuizOptionListResDto> createQuiz(Principal principal,
                                                           @RequestParam Long groupId,
                                                           @CookieValue(value = "guestAccessToken", required = false) String guestToken) {
        QuizOptionListResDto quizOptionListResDto = localQuizService.createQuiz(principal, groupId, guestToken);
        return ApiResTemplate.successResponse(SuccessCode.CREATE_SUCCESS, quizOptionListResDto);
    }

    @Operation(summary = "로컬 퀴즈 답 제출", description = "퀴즈 답을 제출하여 정답 여부를 조회합니다.")
    @PostMapping("/quiz/answer/{gameId}")
    public ApiResTemplate<QuizAnswerResDto> submitQuiz(Principal principal,
                                       @PathVariable Long gameId,
                                       @Valid @RequestBody QuizAnswerReqDto request,
                                       @CookieValue(value = "guestAccessToken", required = false) String guestToken) {
        QuizAnswerResDto quizAnswerResDto = localQuizService.submitQuiz(principal, gameId, request, guestToken);
        return ApiResTemplate.successResponse(SuccessCode.CREATE_SUCCESS, quizAnswerResDto);
    }
}
