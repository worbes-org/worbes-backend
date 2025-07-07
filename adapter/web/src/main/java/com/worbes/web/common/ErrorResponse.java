package com.worbes.web.common;

import java.util.List;

public record ErrorResponse(String code, String message, List<FieldError> errors) {
    
    public record FieldError(String field, String message) {
    }
}
