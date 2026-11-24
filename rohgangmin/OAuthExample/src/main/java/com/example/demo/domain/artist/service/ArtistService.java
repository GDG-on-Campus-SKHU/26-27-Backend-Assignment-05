package com.example.demo.domain.artist.service;

import com.example.demo.domain.artist.dto.ArtistInfoResponseDto;
import com.example.demo.domain.artist.dto.ArtistSaveRequestDto;
import com.example.demo.domain.artist.entity.Artist;
import com.example.demo.domain.artist.repository.ArtistRepository;
import com.example.demo.global.exception.ErrorCode;
import com.example.demo.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ArtistService {
    private final ArtistRepository artistRepository;

    @Transactional
    public ArtistInfoResponseDto saveArtist(ArtistSaveRequestDto artistSaveRequestDto) {
        Artist artist = Artist.builder()
                .stageName(artistSaveRequestDto.stageName())
                .nationality(artistSaveRequestDto.nationality())
                .debutYear(artistSaveRequestDto.debutYear())
                .build();

        artistRepository.save(artist);

        return ArtistInfoResponseDto.from(artist);
    }

    @Transactional
    public void deleteArtist(Long artistId) {
        Artist artist = artistRepository.findById(artistId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.ARTIST_NOT_FOUND));

        artistRepository.delete(artist);
    }

    @Transactional(readOnly = true)
    public List<ArtistInfoResponseDto> getAllArtists() {
        return artistRepository.findAll()
                .stream()
                .map(ArtistInfoResponseDto::from)
                .toList();
    }
}
