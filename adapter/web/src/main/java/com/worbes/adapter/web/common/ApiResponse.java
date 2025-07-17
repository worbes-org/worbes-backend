package com.worbes.adapter.web.common;

public record ApiResponse<T>(
        T content
) {
}
