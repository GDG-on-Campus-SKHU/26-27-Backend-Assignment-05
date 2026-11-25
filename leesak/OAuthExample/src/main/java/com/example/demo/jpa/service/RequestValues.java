package com.example.demo.jpa.service;

import com.example.demo.global.exception.CustomException;
import com.example.demo.global.exception.ErrorCode;

final class RequestValues {
    private RequestValues() {
    }

    static String text(String value, String field, int maxLength) {
        if (value == null || value.isBlank()) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE,
                    field + "은(는) 필수입니다.");
        }

        String trimmed = value.trim();
        if (trimmed.length() > maxLength) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE,
                    field + "은(는) " + maxLength + "자 이하여야 합니다.");
        }
        return trimmed;
    }

    static Long genreId(Long value) {
        if (value == null || value <= 0) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE,
                    "genreId는 양의 정수여야 합니다.");
        }
        return value;
    }
}
