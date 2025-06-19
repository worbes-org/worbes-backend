package com.worbes.web;

public record ApiResponse<T>(
        T contents
) {
}
