package com.example.demo.domain.movie.service;

import com.example.demo.domain.movie.dto.MovieInfoResponseDto;
import com.example.demo.domain.movie.dto.MovieSaveRequestDto;
import com.example.demo.domain.movie.entity.Movie;
import com.example.demo.domain.movie.repository.MovieRepository;
import com.example.demo.domain.user.entity.User;
import com.example.demo.domain.user.repository.UserRepository;
import com.example.demo.global.exception.CustomException;
import com.example.demo.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;
    private final UserRepository userRepository;

    @Transactional
    public MovieInfoResponseDto saveMovie(Long userId, MovieSaveRequestDto movieSaveRequestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() ->  new CustomException(ErrorCode.USER_NOT_FOUND));
        Movie movie = Movie.builder()
                .title(movieSaveRequestDto.title())
                .editor(movieSaveRequestDto.editor())
                .user(user)
                .build();

        movieRepository.save(movie);

        return MovieInfoResponseDto.from(movie);
    }

    @Transactional
    public void deleteMovie(Long userId, Long movieId) {
        Movie movie = movieRepository.findById(movieId)
                        .orElseThrow(() -> new CustomException(ErrorCode.INVALID_INPUT_VALUE));

        if (!movie.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.MOVIE_FORBIDDEN);
        }

        movieRepository.deleteById(movieId);
    }

    @Transactional(readOnly = true)
    public List<MovieInfoResponseDto> getAllMovie() {
        return movieRepository.findAll()
                .stream()
                .map(MovieInfoResponseDto::from)
                .toList();
    }
}
