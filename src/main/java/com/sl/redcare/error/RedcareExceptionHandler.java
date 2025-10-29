package com.sl.redcare.error;

import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class RedcareExceptionHandler {

    @ExceptionHandler(RequestNotPermitted.class)
    public ResponseEntity<RedcareErrorResponse> handleRateLimit(RequestNotPermitted ex) {
        RedcareErrorResponse body = new RedcareErrorResponse(
                "429",
                "Too many requests. Please try again later.",
                "Rate Limit Exceeded"
        );
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<RedcareErrorResponse> handleAll(Exception ex) {
        RedcareErrorResponse body = new RedcareErrorResponse(
                "500",
                "Server is not able to fulfill request at the moment",
                "Internal Server Error"
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

}
