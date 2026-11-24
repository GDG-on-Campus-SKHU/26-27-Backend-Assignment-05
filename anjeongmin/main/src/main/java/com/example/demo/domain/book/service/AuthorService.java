package com.example.demo.domain.book.service;

import com.example.demo.domain.book.domain.Author;
import com.example.demo.domain.book.dto.AuthorInfoResponseDto;
import com.example.demo.domain.book.dto.AuthorSaveRequestDto;
import com.example.demo.domain.book.repository.AuthorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthorService {

    private final AuthorRepository authorRepository;

    @Transactional
    public AuthorInfoResponseDto saveAuthor(AuthorSaveRequestDto authorSaveRequestDto) {
        Author author = Author.builder()
                .name(authorSaveRequestDto.name())
                .nationality(authorSaveRequestDto.nationality())
                .build();

        authorRepository.save(author);

        return AuthorInfoResponseDto.from(author);
    }

    @Transactional
    public void deleteAuthor(Long authorId) {
        authorRepository.deleteById(authorId);
    }

    @Transactional(readOnly = true)
    public List<AuthorInfoResponseDto> getAllAuthorAndBooks() {
        return authorRepository.findAll()
                .stream()
                .map(AuthorInfoResponseDto::from)
                .toList();
    }
}
