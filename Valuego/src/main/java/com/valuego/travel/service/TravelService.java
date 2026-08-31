package com.valuego.travel.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.valuego.global.common.code.ErrorCode;
import com.valuego.global.common.exception.BusinessException;
import com.valuego.global.common.exception.EntityFinderException;
import com.valuego.global.common.exception.ValidMemberException;
import com.valuego.groups.api.dto.response.GroupStatusResDto;
import com.valuego.groups.entity.Group;
import com.valuego.styles.service.GroupStyleService;
import com.valuego.tourplace.api.dto.GroupStyleDto;
import com.valuego.tourplace.api.dto.response.TourPlace;
import com.valuego.tourplace.api.dto.response.TravelScheduleResDto;
import com.valuego.tourplace.service.AiScheduleService;
import com.valuego.tourplace.service.GeminiService;
import com.valuego.tourplace.service.TourApiService;
import com.valuego.tourplace.service.TravelScheduleMapper;
import com.valuego.travel.api.dto.request.AiScheduleUpdateReqDto;
import com.valuego.travel.api.dto.request.TravelPlaceCreateReqDto;
import com.valuego.travel.api.dto.request.TravelPlaceUpdateReqDto;
import com.valuego.travel.api.dto.response.AiScheduleUpdateResDto;
import com.valuego.travel.api.dto.response.TravelPlaceInfoResDto;
import com.valuego.travel.entity.Travel;
import com.valuego.travel.entity.TravelDay;
import com.valuego.travel.entity.TravelPlace;
import com.valuego.travel.entity.repository.TravelPlaceRepository;
import com.valuego.travel.entity.repository.TravelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TravelService {

    private final TravelRepository travelRepository;
    private final EntityFinderException entityFinderException;
    private final ValidMemberException validMemberException;
    private final TravelScheduleMapper travelScheduleMapper;
    private final TourApiService tourApiService;
    private final TravelPlaceRepository travelPlaceRepository;
    private final AiScheduleService aiScheduleService;
    private final GeminiService geminiService;
    private final GroupStyleService groupStyleService;
    private final ObjectMapper objectMapper;

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

    // 장소 직접 추가
    @Transactional
    public TravelPlaceInfoResDto createCustomPlace(Principal principal, TravelPlaceCreateReqDto reqDto, String guestToken) {
        TravelDay travelDay = entityFinderException.getTravelDayById(reqDto.travelDayId());

        Group group = travelDay.getTravel().getGroup();
        validMemberException.validateGroupMember(principal, guestToken, group);

        int targetOrder = (reqDto.scheduleOrder() != null)
                ? reqDto.scheduleOrder()
                : travelDay.getPlaces().size() + 1;

        TravelPlace travelPlace = TravelPlace.builder()
                .travelDay(travelDay)
                .group(group)
                .contentId(null)
                .contentTypeId("CUSTOM")
                .customName(reqDto.customName())
                .visitTime(reqDto.visitTime())
                .memoUrl(reqDto.memoUrl())
                .scheduleOrder(targetOrder)
                .placeType("CUSTOM")
                .reason("직접 추가한 장소입니다.")
                .distanceFromPreviousKm(null)
                .build();

        travelPlaceRepository.save(travelPlace);
        return TravelPlaceInfoResDto.of(travelPlace);
    }

    // 일정 직접 수정
    @Transactional
    public TravelPlaceInfoResDto updatePlace(Principal principal, Long travelPlaceId, TravelPlaceUpdateReqDto reqDto, String guestToken) {
        TravelPlace travelPlace = entityFinderException.getTravelPlaceById(travelPlaceId);
        Group group = travelPlace.getGroup();

        validMemberException.validateGroupMember(principal, guestToken, group);

        travelPlace.updatePlace(reqDto.customName(), reqDto.scheduleOrder(), reqDto.visitTime(), reqDto.memoUrl());

        return TravelPlaceInfoResDto.of(travelPlace);
    }

    @Transactional
    public void deletePlace(Principal principal, Long travelPlaceId, String guestToken) {
        TravelPlace travelPlace = entityFinderException.getTravelPlaceById(travelPlaceId);
        TravelDay travelDay = travelPlace.getTravelDay();
        Group group = travelPlace.getGroup();

        validMemberException.validateGroupMember(principal, guestToken, group);

        travelDay.getPlaces().remove(travelPlace);
        travelPlaceRepository.delete(travelPlace);

        // 남은 장소들 scheduleOrder 1부터 재정렬
        List<TravelPlace> remainingPlaces = travelDay.getPlaces();
        for (int i = 0; i < remainingPlaces.size(); i++) {
            remainingPlaces.get(i).updateScheduleOrder(i + 1);
        }
    }

    // 일정 AI 수정 요청 제안 DB - 미반영
    public AiScheduleUpdateResDto suggestAiScheduleUpdate(Principal principal, AiScheduleUpdateReqDto reqDto, String guestToken) throws Exception {
        Group group = entityFinderException.getGroupById(reqDto.groupId());
        validMemberException.validateGroupMember(principal, guestToken, group);

        Travel currentTravel = travelRepository.findByGroupId(reqDto.groupId())
                .orElseThrow(() -> new BusinessException(ErrorCode.TRAVEL_NOT_FOUND_EXCEPTION
                        , ErrorCode.TRAVEL_NOT_FOUND_EXCEPTION.getMessage()));

        TravelScheduleResDto currentScheduleDto = travelScheduleMapper.toScheduleResDtoWithLiveTourApi(currentTravel);
        String currentScheduleJson = objectMapper.writeValueAsString(currentScheduleDto);

        AiScheduleService.TourCandidatesResult candidates = aiScheduleService.fetchTourCandidates(group);
        List<GroupStyleDto> styles = groupStyleService.getGroupStyles(reqDto.groupId());

        return geminiService.generateUpdate(
                group.getDestination(),
                reqDto.dayNum(),
                styles,
                candidates.getActivities(),
                candidates.getRestaurants(),
                currentScheduleJson,
                reqDto.prompt()
        );
    }

    // 일정 AI 수정 요청 확정
    @Transactional
    public TravelScheduleResDto applyAiScheduleUpdate(Principal principal, Long groupId, AiScheduleUpdateResDto suggestedSchedule, String guestToken) {
        Group group = entityFinderException.getGroupById(groupId);
        validMemberException.validateGroupMember(principal, guestToken, group);

        return aiScheduleService.saveSuggestedSchedule(groupId, group, suggestedSchedule);
    }
}
