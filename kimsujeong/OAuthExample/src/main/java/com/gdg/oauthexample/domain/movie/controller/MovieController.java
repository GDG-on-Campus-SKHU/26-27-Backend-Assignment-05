package com.gdg.oauthexample.domain.movie.controller;

import com.gdg.oauthexample.domain.movie.dto.MovieInfoResponseDto;
import com.gdg.oauthexample.domain.movie.dto.MovieSaveRequestDto;
import com.gdg.oauthexample.domain.movie.dto.MovieUpdateRequestDto;
import com.gdg.oauthexample.domain.movie.service.MovieService;
import com.gdg.oauthexample.domain.user.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<MovieInfoResponseDto> saveMovie(@Valid @AuthenticationPrincipal Long userId,
                                                          @Valid @RequestBody MovieSaveRequestDto movieSaveRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(movieService.saveMovie(userId, movieSaveRequestDto));
    }

    @PatchMapping("/{movieId}")
    public ResponseEntity<MovieInfoResponseDto> updateMovie(@Valid @AuthenticationPrincipal Long userId,
                                                            @Valid @PathVariable Long movieId,
                                                            @Valid @RequestBody MovieUpdateRequestDto movieUpdateRequestDto) {

        return ResponseEntity.status(HttpStatus.OK).body(movieService.updateMovie(userId, movieId, movieUpdateRequestDto));
    }

    @DeleteMapping("/{movieId}")
    public ResponseEntity<Void> deleteMovie(@Valid @AuthenticationPrincipal Long userId,
                                            @Valid @PathVariable Long movieId) {
        movieService.deleteMovie(userId, movieId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping
    public ResponseEntity<List<MovieInfoResponseDto>> getAllMovie() {
        return ResponseEntity.status(HttpStatus.OK).body(movieService.getAllMovie());
    }
}
