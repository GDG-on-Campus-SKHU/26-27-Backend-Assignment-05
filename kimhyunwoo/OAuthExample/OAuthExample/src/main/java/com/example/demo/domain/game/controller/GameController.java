package com.example.demo.domain.game.controller;

import com.example.demo.domain.game.dto.GameInfoResponseDto;
import com.example.demo.domain.game.dto.GameSaveRequestDto;
import com.example.demo.domain.game.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/games")
public class GameController {

    private final GameService gameService;

    @PostMapping
    public ResponseEntity<GameInfoResponseDto> saveGame(@RequestBody GameSaveRequestDto gameSaveRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(gameService.saveGame(gameSaveRequestDto));
    }

    @DeleteMapping("/{gameId}")
    public ResponseEntity<Void> deleteGame(@PathVariable Long gameId) {
        gameService.deleteGame(gameId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping
    public ResponseEntity<List<GameInfoResponseDto>> getAllGames() {
        return ResponseEntity.status(HttpStatus.OK).body(gameService.getAllGames());
    }
}
