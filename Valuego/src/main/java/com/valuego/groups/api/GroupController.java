package com.valuego.groups.api;

import com.valuego.global.common.code.SuccessCode;
import com.valuego.global.common.template.ApiResTemplate;
import com.valuego.groups.api.dto.reqest.GroupCreateReqDto;
import com.valuego.groups.api.dto.reqest.GroupGuestInfoReqDto;
import com.valuego.groups.api.dto.response.GroupGuestJoinResDto;
import com.valuego.groups.api.dto.response.GroupInfoResDto;
import com.valuego.groups.service.GroupMemberService;
import com.valuego.groups.service.GroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/groups")
@Tag(name = "Group API", description = "그룹 관련 API")
public class GroupController {

    private final GroupService groupService;
    private final GroupMemberService groupMemberService;

    @Operation(summary = "그룹 생성", description = "로그인한 팀장이 그룹을 생성합니다.")
    @PostMapping
    public ApiResTemplate<GroupInfoResDto> createGroup(Principal principal,
                                                       @RequestBody GroupCreateReqDto groupCreateReqDto) {
        GroupInfoResDto groupInfoResDto = groupService.createGroup(principal, groupCreateReqDto);
        return ApiResTemplate.successResponse(SuccessCode.CREATE_SUCCESS, groupInfoResDto);
    }

    @Operation(summary = "게스트 초대", description = "로그인한 팀장이 링크를 복사하여 게스트를 초대합니다.\n " +
            "게스트가 해당 링크로 참여시 해당 그룹의 멤버로 자동으로 등록됨")
    @PostMapping("/invite")
    public ResponseEntity<ApiResTemplate<GroupGuestJoinResDto>> inviteGroup(@RequestParam String groupLink,
                                                                           @RequestBody GroupGuestInfoReqDto groupGuestInfoReqDto) {
        return groupMemberService.inviteGroup(groupLink, groupGuestInfoReqDto);
    }

    @Operation(summary = "그룹 상세 정보 조회", description = "로그인한 팀장이 그룹 상세 정보를 조회합니다.\n " +
            "팀장만 조회 가능")
    @GetMapping
    public ApiResTemplate<GroupInfoResDto> getDetailGroup(Principal principal, @RequestParam Long groupId) {
        GroupInfoResDto groupInfoResDto = groupService.getDetailGroup(principal, groupId);
        return ApiResTemplate.successResponse(SuccessCode.GET_SUCCESS, groupInfoResDto);
    }
}
