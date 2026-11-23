package com.example.demo.domain.movie.controller;

import com.example.demo.domain.movie.dto.MovieInfoResponseDto;
import com.example.demo.domain.movie.dto.MovieSaveRequestDto;
import com.example.demo.domain.movie.service.MovieService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/movies")
public class MovieController {

    private final MovieService movieService;

    @PostMapping
    public ResponseEntity<MovieInfoResponseDto> saveMovie(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody MovieSaveRequestDto movieSaveRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(movieService.saveMovie(userId, movieSaveRequestDto));
    }

    @DeleteMapping("/{movieId}")
    public ResponseEntity<Void> deleteMovie(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long movieId) {
        movieService.deleteMovie(userId, movieId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping
    public ResponseEntity<List<MovieInfoResponseDto>> getAllMovie() {
        return ResponseEntity.status(HttpStatus.OK).body(movieService.getAllMovie());
    }
}
