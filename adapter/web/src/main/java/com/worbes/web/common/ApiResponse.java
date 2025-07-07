package com.worbes.web.common;

public record ApiResponse<T>(
        T content
) {
}
