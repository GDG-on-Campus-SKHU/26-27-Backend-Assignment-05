package com.example.demo.jpa.service;

import com.example.demo.jpa.domain.Book;
import com.example.demo.jpa.domain.Genre;
import com.example.demo.jpa.dto.BookRequest;
import com.example.demo.jpa.dto.BookResponse;
import com.example.demo.jpa.repository.BookRepository;
import com.example.demo.jpa.repository.GenreRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.demo.global.exception.ErrorCode;
import com.example.demo.global.exception.NotFoundException;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class BookService {
    private final BookRepository books;
    private final GenreRepository genres;

    public BookService(BookRepository books, GenreRepository genres) {
        this.books = books;
        this.genres = genres;
    }

    @Transactional
    public BookResponse create(BookRequest request) {
        String title = RequestValues.text(request.title(), "title", 200);
        String author = RequestValues.text(request.author(), "author", 120);
        Genre genre = getGenre(RequestValues.genreId(request.genreId()));
        return BookResponse.from(books.save(new Book(title, author, genre)));
    }

    public List<BookResponse> findAll() {
        return books.findAll().stream().map(BookResponse::from).toList();
    }

    public BookResponse findById(Long id) {
        return BookResponse.from(getBook(id));
    }

    @Transactional
    public BookResponse update(Long id, BookRequest request) {
        Book book = getBook(id);
        if (request.title() != null) {
            book.updateTitle(RequestValues.text(request.title(), "title", 200));
        }
        if (request.author() != null) {
            book.updateAuthor(RequestValues.text(request.author(), "author", 120));
        }
        if (request.genreId() != null) {
            book.changeGenre(getGenre(RequestValues.genreId(request.genreId())));
        }
        return BookResponse.from(book);
    }

    @Transactional
    public void delete(Long id) {
        books.delete(getBook(id));
    }

    private Book getBook(Long id) {
        return books.findById(id).orElseThrow(() ->
                new NotFoundException(ErrorCode.BOOK_NOT_FOUND));
    }

    private Genre getGenre(Long id) {
        return genres.findById(id).orElseThrow(() ->
                new NotFoundException(ErrorCode.GENRE_NOT_FOUND));
    }
}
