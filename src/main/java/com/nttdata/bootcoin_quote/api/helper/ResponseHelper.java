package com.nttdata.bootcoin_quote.api.helper;

import lombok.experimental.UtilityClass;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@UtilityClass
public class ResponseHelper {
    
    public <T> ResponseEntity<T> buildAcceptedWithCorrelation(T body, String correlationId) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Correlation-Id", correlationId);
        return new ResponseEntity<>(body, headers, HttpStatus.ACCEPTED);
    }
}