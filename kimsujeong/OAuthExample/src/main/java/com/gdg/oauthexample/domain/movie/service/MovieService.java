package com.gdg.oauthexample.domain.movie.service;

import com.gdg.oauthexample.domain.movie.dto.MovieInfoResponseDto;
import com.gdg.oauthexample.domain.movie.dto.MovieSaveRequestDto;
import com.gdg.oauthexample.domain.movie.entity.Movie;
import com.gdg.oauthexample.domain.movie.repository.MovieRepository;
import com.gdg.oauthexample.domain.user.entity.User;
import com.gdg.oauthexample.domain.user.repository.UserRepository;
import com.gdg.oauthexample.global.exception.ErrorCode;
import com.gdg.oauthexample.global.exception.ForbiddenException;
import com.gdg.oauthexample.global.exception.NotFoundException;
import com.gdg.oauthexample.global.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;
    private final UserRepository userRepository;

    // saveMovie
    @Transactional
    public MovieInfoResponseDto saveMovie(Long userId, MovieSaveRequestDto movieSaveRequestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        Movie movie = Movie.builder()
                .title(movieSaveRequestDto.title())
                .director(movieSaveRequestDto.director())
                .releaseDate(movieSaveRequestDto.releaseDate())
                .user(user)
                .build();

        movieRepository.save(movie);

        return MovieInfoResponseDto.from(movie);
    }

    // updateMovie
    @Transactional
    public MovieInfoResponseDto updateMovie(Long userId, Long movieId, MovieSaveRequestDto movieSaveRequestDto) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new IllegalArgumentException(("요청하신 영화 정보를 찾을 수 없습니다.")));

        // 영화 소유자와 userId가 같은지 확인
        if (!movie.getUser().getId().equals(userId)) {
            throw new ForbiddenException(ErrorCode.MOVIE_PATCH_FORBIDDEN);
        }

        movie.update(movieSaveRequestDto.title(), movieSaveRequestDto.director(), movieSaveRequestDto.releaseDate());

        return MovieInfoResponseDto.from(movie);
    }

    // deleteMovie
    @Transactional
    public void deleteMovie(Long userId, Long movieId) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 영화입니다."));

        // 영화 소유자와 userId가 같은지 확인
        if (!movie.getUser().getId().equals(userId)) {
            throw new ForbiddenException(ErrorCode.MOVIE_DELETE_FORBIDDEN);
        }

        movieRepository.delete(movie);
    }

    // getAllMovie
    @Transactional(readOnly = true)
    public List<MovieInfoResponseDto> getAllMovie() {
        return movieRepository.findAll()
                .stream()
                .map(MovieInfoResponseDto::from)
                .toList();
    }
}
