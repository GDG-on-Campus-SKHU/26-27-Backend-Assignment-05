package com.example.demo.jpa.service;

import com.example.demo.jpa.domain.Genre;
import com.example.demo.jpa.dto.GenreRequest;
import com.example.demo.jpa.dto.GenreResponse;
import com.example.demo.jpa.repository.BookRepository;
import com.example.demo.jpa.repository.GenreRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.example.demo.global.exception.ConflictException;
import com.example.demo.global.exception.ErrorCode;
import com.example.demo.global.exception.NotFoundException;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class GenreService {
    private final GenreRepository genres;
    private final BookRepository books;

    public GenreService(GenreRepository genres, BookRepository books) {
        this.genres = genres;
        this.books = books;
    }

    @Transactional
    public GenreResponse create(GenreRequest request) {
        Genre genre = new Genre(RequestValues.text(request.name(), "name", 100));
        return GenreResponse.from(genres.save(genre));
    }

    public List<GenreResponse> findAll() {
        return genres.findAll().stream().map(GenreResponse::from).toList();
    }

    public GenreResponse findById(Long id) {
        return GenreResponse.from(getGenre(id));
    }

    @Transactional
    public GenreResponse update(Long id, GenreRequest request) {
        Genre genre = getGenre(id);
        if (request.name() != null) {
            genre.updateName(RequestValues.text(request.name(), "name", 100));
        }
        return GenreResponse.from(genre);
    }

    @Transactional
    public void delete(Long id) {
        Genre genre = getGenre(id);
        if (books.existsByGenreId(id)) {
            throw new ConflictException(ErrorCode.GENRE_IN_USE);
        }
        genres.delete(genre);
    }

    private Genre getGenre(Long id) {
        return genres.findById(id).orElseThrow(() ->
                new NotFoundException(ErrorCode.GENRE_NOT_FOUND));
    }
}
