package com.nttdata.bootcoin_quote.api.helper;

import lombok.experimental.UtilityClass;
import org.springframework.web.server.ServerWebExchange;

import java.util.Optional;
import java.util.UUID;

@UtilityClass
public class CorrelationHelper {
    
    public String extractOrGenerate(ServerWebExchange exchange) {
        return Optional
                .ofNullable(exchange.getRequest().getHeaders().getFirst("X-Correlation-Id"))
                .filter(h -> !h.isBlank())
                .orElse(UUID.randomUUID().toString());
    }
}