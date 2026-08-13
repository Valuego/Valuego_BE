package com.valuego.games.api;

import com.valuego.games.api.dto.request.GameCreateReqDto;
import com.valuego.games.api.dto.response.GameMemberListResDto;
import com.valuego.games.api.dto.response.GameResDto;
import com.valuego.games.service.GameService;
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

    @Operation(summary = "사다리 타기", description = "그룹 id로 판별하여 벌칙 작성 후 사다리 타기")
    @PostMapping("/ladder")
    public GameResDto createLadder(Principal principal,
                                   @RequestParam Long groupId,
                                   @Valid @RequestBody GameCreateReqDto gameCreateReqDto,
                                   @CookieValue(value = "guestAccessToken", required = false) String guestToken) {
        return gameService.createLadder(principal, groupId, gameCreateReqDto, guestToken);
    }

    @Operation(summary = "룰렛", description = "그룹 id로 판별하여 벌칙 생성 후 룰렛 돌리기")
    @PostMapping("/roulette")
    public GameResDto createRoulette(Principal principal,
                                     @RequestParam Long groupId,
                                     @Valid @RequestBody GameCreateReqDto gameCreateReqDto,
                                     @CookieValue(value = "guestAccessToken", required = false) String guestToken) {
        return gameService.createRoulette(principal, groupId, gameCreateReqDto, guestToken);
    }

    @Operation(summary = "멤버 리스트 조회", description = "게임을 하기 위한 그룹별 멤버 리스트를 조회합니다.")
    @PostMapping("/members")
    public List<GameMemberListResDto> getGameMembers(Principal principal,
                                                     @RequestParam Long groupId,
                                                     @CookieValue(value = "guestToken", required = false) String guestToken) {
        return gameService.getGameMembers(principal, groupId, guestToken);
    }
}
