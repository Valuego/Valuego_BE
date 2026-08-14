package com.valuego.games.service;

import com.valuego.games.entity.Game;
import com.valuego.games.entity.GameType;
import com.valuego.games.entity.repository.GameRepository;
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
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class LocalQuizService {

    private final GameRepository gameRepository;
    private final EntityFinderException entityFinderException;
    private final ValidMemberException validMemberException;
    private final GeminiGameService geminiGameService;
    private final LocalQuizRepository localQuizRepository;

    // local quiz
    public QuizOptionListResDto createQuiz(Principal principal, Long groupId, String guestToken) {
        Group group = entityFinderException.getGroupById(groupId);
        validMemberException.validateGroupMember(principal, guestToken, group);

        QuizAiResDto quizAiResDto = geminiGameService.generateLocalQuiz(group.getDestination());

        Game game = Game.builder()
                .group(group)
                .gameType(GameType.QUIZ)
                .result(List.of())
                .build();

        gameRepository.save(game);

        LocalQuiz localQuiz = createLocalQuiz(game, quizAiResDto);

        localQuizRepository.save(localQuiz);

        return QuizOptionListResDto.from(
                game.getId(),
                group.getId(),
                localQuiz
        );
    }

    private LocalQuiz createLocalQuiz(Game game, QuizAiResDto quizAiResDto) {
        return LocalQuiz.builder()
                .game(game)
                .question(quizAiResDto.question())
                .optionA(getOption(quizAiResDto, "A"))
                .optionB(getOption(quizAiResDto, "B"))
                .optionC(getOption(quizAiResDto, "C"))
                .optionD(getOption(quizAiResDto, "D"))
                .correctOption(quizAiResDto.correctOption())
                .explanation(quizAiResDto.explanation())
                .build();
    }

    private String getOption(QuizAiResDto quizAiResDto, String option) {
        return quizAiResDto.options()
                .stream()
                .filter(item -> item.option().equalsIgnoreCase(option))
                .findFirst()
                .orElseThrow(() ->
                        new BusinessException(
                                ErrorCode.GAME_QUIZ_CREATE_EXCEPTION,
                                ErrorCode.GAME_QUIZ_CREATE_EXCEPTION.getMessage()
                        )
                )
                .content();
    }

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
