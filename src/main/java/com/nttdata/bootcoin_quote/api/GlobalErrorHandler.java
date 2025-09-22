package com.nttdata.bootcoin_quote.api;

import com.nttdata.model.Problem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

@Slf4j
@RestControllerAdvice
public class GlobalErrorHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public Mono<ResponseEntity<Problem>> handleNotFound(ResourceNotFoundException ex) {
        log.warn("Resource not found: {}", ex.getMessage());

        Problem problem = new Problem()
                .type("about:blank")
                .title("Not Found")
                .status(404)
                .detail(ex.getMessage())
                .instance("/api/v1/bootcoin/quote/rate");

        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Mono<ResponseEntity<Problem>> handleBadRequest(IllegalArgumentException ex) {
        log.warn("Bad request: {}", ex.getMessage());

        Problem problem = new Problem()
                .type("about:blank")
                .title("Bad Request")
                .status(400)
                .detail(ex.getMessage())
                .instance("/api/v1/bootcoin/quote/rate");

        return Mono.just(ResponseEntity.badRequest().body(problem));
    }

    @ExceptionHandler(IllegalStateException.class)
    public Mono<ResponseEntity<Problem>> handleConflict(IllegalStateException ex) {
        log.warn("Conflict: {}", ex.getMessage());

        Problem problem = new Problem()
                .type("about:blank")
                .title("Conflict")
                .status(409)
                .detail(ex.getMessage())
                .instance("/api/v1/bootcoin/quote/rate");

        return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).body(problem));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<Problem>> handleGenericError(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);

        Problem problem = new Problem()
                .type("about:blank")
                .title("Internal Server Error")
                .status(500)
                .detail("An unexpected error occurred")
                .instance("/api/v1/bootcoin/quote/rate");

        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problem));
    }
}