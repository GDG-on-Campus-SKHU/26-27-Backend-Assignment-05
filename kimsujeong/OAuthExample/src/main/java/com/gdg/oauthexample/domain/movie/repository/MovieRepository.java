package com.gdg.oauthexample.domain.movie.repository;

import com.gdg.oauthexample.domain.movie.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository extends JpaRepository<Movie, Long> {
}
