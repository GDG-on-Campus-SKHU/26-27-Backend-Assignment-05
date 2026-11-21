package com.example.demo.domain.book.controller;

import com.example.demo.domain.book.dto.BookInfoResponseDto;
import com.example.demo.domain.book.dto.BookSaveRequestDto;
import com.example.demo.domain.book.dto.BookUpdateRequestDto;
import com.example.demo.domain.book.service.BookService;
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
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;

    @PostMapping
    public ResponseEntity<BookInfoResponseDto> saveBook(@AuthenticationPrincipal Long userId,
                                                        @RequestBody BookSaveRequestDto bookSaveRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookService.saveBook(bookSaveRequestDto));
    }

    @GetMapping("/{bookId}")
    public ResponseEntity<BookInfoResponseDto> getBook(@AuthenticationPrincipal Long userId,
                                                       @PathVariable Long bookId) {
        return ResponseEntity.status(HttpStatus.OK).body(bookService.getBook(bookId));
    }

    @PatchMapping("/{bookId}")
    public ResponseEntity<BookInfoResponseDto> updateBook(@AuthenticationPrincipal Long userId,
                                                          @PathVariable Long bookId,
                                                          @RequestBody BookUpdateRequestDto bookUpdateRequestDto) {
        return ResponseEntity.status(HttpStatus.OK).body(bookService.updateBook(bookId, bookUpdateRequestDto));
    }

    @DeleteMapping("/{bookId}")
    public ResponseEntity<Void> deleteBook(@AuthenticationPrincipal Long userId,
                                           @PathVariable Long bookId) {
        bookService.deleteBook(bookId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping
    public ResponseEntity<List<BookInfoResponseDto>> getAllBook(@AuthenticationPrincipal Long userId) {
        return ResponseEntity.status(HttpStatus.OK).body(bookService.getAllBook());
    }
}
