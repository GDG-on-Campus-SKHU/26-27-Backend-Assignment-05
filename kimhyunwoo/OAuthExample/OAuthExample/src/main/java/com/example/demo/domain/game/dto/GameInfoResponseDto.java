package com.example.demo.domain.game.dto;

import com.example.demo.domain.game.entity.Game;
import lombok.Builder;

@Builder
public record GameInfoResponseDto(
        Long id,
        String gameName,
        String genre
) {
    public static GameInfoResponseDto from(Game game) {
        return GameInfoResponseDto.builder()
                .id(game.getId())
                .gameName(game.getGameName())
                .genre(game.getGenre())
                .build();
    }
}
