package com.example.demo.domain.book.service;

import com.example.demo.domain.book.entity.Book;
import com.example.demo.domain.book.dto.BookInfoResponseDto;
import com.example.demo.domain.book.dto.BookSaveRequestDto;
import com.example.demo.domain.book.dto.BookUpdateRequestDto;
import com.example.demo.domain.book.repository.BookRepository;
import com.example.demo.global.exception.ErrorCode;
import com.example.demo.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    @Transactional
    public BookInfoResponseDto saveBook(BookSaveRequestDto bookSaveRequestDto) {
        Book book = Book.builder()
                .title(bookSaveRequestDto.title())
                .price(bookSaveRequestDto.price())
                .author(bookSaveRequestDto.author())
                .build();

        bookRepository.save(book);

        return BookInfoResponseDto.from(book);
    }

    @Transactional(readOnly = true)
    public BookInfoResponseDto getBook(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.BOOK_NOT_FOUND));

        return BookInfoResponseDto.from(book);
    }

    @Transactional
    public BookInfoResponseDto updateBook(Long bookId, BookUpdateRequestDto bookUpdateRequestDto) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.BOOK_NOT_FOUND));

        book.update(bookUpdateRequestDto.title(),
                bookUpdateRequestDto.price());

        return BookInfoResponseDto.from(book);
    }

    @Transactional
    public void deleteBook(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.BOOK_NOT_FOUND));

        bookRepository.delete(book);
    }

    @Transactional(readOnly = true)
    public List<BookInfoResponseDto> getAllBook() {
        List<Book> books = bookRepository.findAll();
        List<BookInfoResponseDto> result = new ArrayList<>();

        for (Book book : books) {
            result.add(BookInfoResponseDto.from(book));
        }

        return result;
    }
}
