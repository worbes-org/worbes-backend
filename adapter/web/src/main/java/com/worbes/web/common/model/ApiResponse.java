package com.worbes.web.common.model;

public record ApiResponse<T>(
        T content
) {
}
