package com.one.yjh.domain.dto;

import lombok.Builder;

@Builder
public record TokenResponse (
    Long id,
    String accessToken,
    String refreshToken
) {}