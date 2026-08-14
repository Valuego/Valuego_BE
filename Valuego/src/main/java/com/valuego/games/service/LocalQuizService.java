package com.valuego.games.service;

import com.valuego.games.entity.Game;
import com.valuego.games.quiz.dto.response.QuizAiResDto;
import com.valuego.games.quiz.dto.response.QuizOptionListResDto;
import com.valuego.games.quiz.dto.request.QuizAnswerReqDto;
import com.valuego.games.quiz.dto.response.QuizAnswerResDto;
import com.valuego.games.quiz.entity.LocalQuiz;
import com.valuego.games.quiz.entity.repository.LocalQuizRepository;
import com.valuego.global.common.code.ErrorCode;
import com.valuego.global.common.exception.BusinessException;
import com.valuego.global.common.exception.EntityFinderException;
import com.valuego.global.common.exception.ValidMemberException;
import com.valuego.groups.entity.Group;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
@RequiredArgsConstructor
public class LocalQuizService {

    private final EntityFinderException entityFinderException;
    private final ValidMemberException validMemberException;
    private final GeminiGameService geminiGameService;
    private final LocalQuizRepository localQuizRepository;
    private final LocalQuizSaveService localQuizSaveService;

    // local quiz 생성
    public QuizOptionListResDto createQuiz(Principal principal, Long groupId, String guestToken) {
        Group group = entityFinderException.getGroupById(groupId);
        validMemberException.validateGroupMember(principal, guestToken, group);

        // gemini api
        QuizAiResDto quizAiResDto = geminiGameService.generateLocalQuiz(group.getDestination());

        return localQuizSaveService.saveQuiz(groupId, quizAiResDto);
    }

    // quiz 답 제출
    public QuizAnswerResDto submitQuiz(Principal principal, Long gameId, QuizAnswerReqDto request, String guestToken) {
        Game game = entityFinderException.getGameById(gameId);
        validMemberException.validateGroupMember(principal, guestToken, game.getGroup());

        LocalQuiz quiz = localQuizRepository.findByGame(game).orElseThrow(
                () -> new BusinessException(ErrorCode.GAME_QUIZ_NOT_FOUND_EXCEPTION
                        , ErrorCode.GAME_QUIZ_NOT_FOUND_EXCEPTION.getMessage()));

        boolean correct = quiz.getCorrectOption().equalsIgnoreCase(request.selectedOption());

        return new QuizAnswerResDto(
                correct,
                request.selectedOption(),
                quiz.getCorrectOption(),
                quiz.getExplanation()
        );
    }
}
