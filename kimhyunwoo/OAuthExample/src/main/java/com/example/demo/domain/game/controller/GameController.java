package com.example.demo.domain.game.controller;

import com.example.demo.domain.game.dto.GameInfoResponseDto;
import com.example.demo.domain.game.dto.GameSaveRequestDto;
import com.example.demo.domain.game.service.GameService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/games")
public class GameController {

    private final GameService gameService;

    @PostMapping
    public ResponseEntity<GameInfoResponseDto> saveGame(@AuthenticationPrincipal Long userId,
                                                        @Valid @RequestBody GameSaveRequestDto gameSaveRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(gameService.saveGame(userId, gameSaveRequestDto));
    }

    @DeleteMapping("/{gameId}")
    public ResponseEntity<Void> deleteGame(@PathVariable Long gameId,
                                           @AuthenticationPrincipal Long userId) {
        gameService.deleteGame(gameId, userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping
    public ResponseEntity<List<GameInfoResponseDto>> getAllGames() {
        return ResponseEntity.status(HttpStatus.OK).body(gameService.getAllGames());
    }
}
