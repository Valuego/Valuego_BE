package com.valuego.travel.service;

import com.valuego.global.common.code.ErrorCode;
import com.valuego.global.common.exception.BusinessException;
import com.valuego.global.common.exception.EntityFinderException;
import com.valuego.global.common.exception.ValidMemberException;
import com.valuego.groups.api.dto.response.GroupStatusResDto;
import com.valuego.groups.entity.Group;
import com.valuego.tourplace.api.dto.response.TourPlace;
import com.valuego.tourplace.api.dto.response.TravelScheduleResDto;
import com.valuego.tourplace.service.TourApiService;
import com.valuego.tourplace.service.TravelScheduleMapper;
import com.valuego.travel.api.dto.response.TravelPlaceInfoResDto;
import com.valuego.travel.entity.Travel;
import com.valuego.travel.entity.TravelPlace;
import com.valuego.travel.entity.repository.TravelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TravelService {

    private final TravelRepository travelRepository;
    private final EntityFinderException entityFinderException;
    private final ValidMemberException validMemberException;
    private final TravelScheduleMapper travelScheduleMapper;
    private final TourApiService tourApiService;

    // 전체 일정 조회
    public TravelScheduleResDto getAllSchedule(Principal principal, Long groupId, String guestToken) {
        Group group = entityFinderException.getGroupById(groupId);
        validMemberException.validateGroupMember(principal, guestToken, group);

        Travel travel = travelRepository.findByGroupId(groupId)
                .orElseThrow(()-> new BusinessException(ErrorCode.TRAVEL_NOT_FOUND_EXCEPTION
                        , ErrorCode.TRAVEL_NOT_FOUND_EXCEPTION.getMessage()));

        return travelScheduleMapper.toScheduleResDtoWithLiveTourApi(travel);
    }

    // 상세 일정 조회
    public TravelPlaceInfoResDto getDetailSchedule(Principal principal, Long travelPlaceId, String guestToken) {
        TravelPlace travelPlace = entityFinderException.getTravelPlaceById(travelPlaceId);
        Group group = travelPlace.getTravelDay().getTravel().getGroup();

        validMemberException.validateGroupMember(principal, guestToken, group);

        TourPlace liveData = tourApiService.getPlaceDetail(travelPlace.getContentId());

        return TravelPlaceInfoResDto.of(travelPlace, liveData);
    }

    // 일정 확정
    @Transactional
    public GroupStatusResDto confirmSchedule(Principal principal, Long groupId, String guestToken) {
        Group group = entityFinderException.getGroupById(groupId);
        validMemberException.validateGroupMember(principal, guestToken, group);

        travelRepository.findByGroupId(groupId)
                .orElseThrow(()-> new BusinessException(ErrorCode.TRAVEL_NOT_FOUND_EXCEPTION
                        , ErrorCode.TRAVEL_NOT_FOUND_EXCEPTION.getMessage()));

        group.confirmSchedule();

        return GroupStatusResDto.from(group);
    }
}
