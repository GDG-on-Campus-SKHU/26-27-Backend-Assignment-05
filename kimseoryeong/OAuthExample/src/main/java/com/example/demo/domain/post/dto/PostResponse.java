package com.example.demo.domain.post.dto;

import com.example.demo.domain.post.entity.Post;

public record PostResponse(
        Long id,
        String title,
        String content,
        Long writerId
) {

    public static PostResponse from(Post post) {
        return new PostResponse(post.getId(), post.getTitle(), post.getContent(), post.getWriterId());
    }
}
