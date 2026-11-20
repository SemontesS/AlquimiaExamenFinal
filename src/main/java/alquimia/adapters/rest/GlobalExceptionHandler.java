package alquimia.adapters.rest;

import alquimia.adapters.rest.dto.ErrorResponse;
import alquimia.domain.exceptions.DeviceReportNotFoundException;
import alquimia.domain.exceptions.DuplicateDeviceReportException;
import alquimia.domain.exceptions.InsufficientDataException;
import alquimia.domain.exceptions.InvalidDeviceDataException;
import alquimia.domain.exceptions.UnknownDeviceException;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** Translates domain errors into HTTP codes. The domain itself knows nothing about HTTP. */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({InvalidDeviceDataException.class, UnknownDeviceException.class})
    ResponseEntity<ErrorResponse> badRequest(RuntimeException ex, HttpServletRequest request) {
        return respond(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    @ExceptionHandler(DeviceReportNotFoundException.class)
    ResponseEntity<ErrorResponse> notFound(RuntimeException ex, HttpServletRequest request) {
        return respond(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(DuplicateDeviceReportException.class)
    ResponseEntity<ErrorResponse> conflict(RuntimeException ex, HttpServletRequest request) {
        return respond(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    @ExceptionHandler(InsufficientDataException.class)
    ResponseEntity<ErrorResponse> insufficient(RuntimeException ex, HttpServletRequest request) {
        return respond(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ErrorResponse> validation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String detail = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + " " + e.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return respond(HttpStatus.BAD_REQUEST, "Validation failed: " + detail, request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<ErrorResponse> malformed(HttpMessageNotReadableException ex, HttpServletRequest request) {
        return respond(HttpStatus.BAD_REQUEST, "Malformed request body.", request);
    }

    private ResponseEntity<ErrorResponse> respond(HttpStatus status, String message, HttpServletRequest request) {
        return ResponseEntity.status(status).body(new ErrorResponse(Instant.now(), status.value(),
                status.getReasonPhrase(), message, request.getRequestURI()));
    }
}
