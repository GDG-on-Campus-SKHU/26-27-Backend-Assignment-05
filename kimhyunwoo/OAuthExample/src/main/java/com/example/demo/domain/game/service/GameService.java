package com.example.demo.domain.game.service;

import com.example.demo.domain.game.dto.GameInfoResponseDto;
import com.example.demo.domain.game.dto.GameSaveRequestDto;
import com.example.demo.domain.game.entity.Game;
import com.example.demo.domain.game.repository.GameRepository;
import com.example.demo.global.exception.ErrorCode;
import com.example.demo.global.exception.ForbiddenException;
import com.example.demo.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;

    @Transactional
    public GameInfoResponseDto saveGame(Long userId, GameSaveRequestDto gameSaveRequestDto) {
        Game game = Game.builder()
                .gameName(gameSaveRequestDto.gameName())
                .genre(gameSaveRequestDto.genre())
                .writerId(userId)
                .build();

        gameRepository.save(game);

        return GameInfoResponseDto.from(game);
    }

    @Transactional
    public void deleteGame(Long gameId, Long userId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.GAME_NOT_FOUND));

        if (!game.getWriterId().equals(userId)) {
            throw new ForbiddenException(ErrorCode.GAME_FORBIDDEN);
        }

        gameRepository.delete(game);
    }

    @Transactional(readOnly = true)
    public List<GameInfoResponseDto> getAllGames() {
        return gameRepository.findAll()
                .stream()
                .map(GameInfoResponseDto::from)
                .toList();
    }
}
