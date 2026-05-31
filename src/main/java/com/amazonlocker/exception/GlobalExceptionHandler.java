package com.amazonlocker.exception;

import com.amazonlocker.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PinInvalidException.class)
    public ResponseEntity<ErrorResponse> handlePinInvalid(PinInvalidException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("pin", ex.getMessage()));
    }

    @ExceptionHandler(CompartmentNotAvailableException.class)
    public ResponseEntity<ErrorResponse> handleNotAvailable(CompartmentNotAvailableException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("compartment", ex.getMessage()));
    }

    @ExceptionHandler(CompartmentNotOccupiedException.class)
    public ResponseEntity<ErrorResponse> handleNotOccupied(CompartmentNotOccupiedException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("compartment", ex.getMessage()));
    }

    @ExceptionHandler(CompartmentNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(CompartmentNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("compartment", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        String field = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getField())
                .orElse("request");
        String error = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(fe -> fe.getDefaultMessage())
                .orElse("Invalid request value");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(field, error));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadable(HttpMessageNotReadableException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("request", "Invalid request value"));
    }
}
