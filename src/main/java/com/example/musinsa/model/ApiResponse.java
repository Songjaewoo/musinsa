package com.example.musinsa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private int code;
    private T data;
    private String message;

    public static <T> ApiResponse<T> success(HttpStatus status, T data) {
        return new ApiResponse<>(status.value(), data, null);
    }

    public static <T> ApiResponse<T> success(HttpStatus status, String message) {
        return new ApiResponse<>(status.value(), null, message);
    }

    public static <T> ApiResponse<T> failure(HttpStatus status, String message) {
        return new ApiResponse<>(status.value(), null, message);
    }
}
