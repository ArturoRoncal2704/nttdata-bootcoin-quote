package com.nttdata.bootcoin_quote.api;

import com.nttdata.bootcoin_quote.api.helper.CorrelationHelper;
import com.nttdata.bootcoin_quote.api.helper.ReactiveAdapter;
import com.nttdata.bootcoin_quote.api.helper.ResponseHelper;
import com.nttdata.bootcoin_quote.service.QuoteService;
import com.nttdata.controller.QuoteApi;
import com.nttdata.model.QuoteAccepted;
import com.nttdata.model.QuoteRates;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
public class RatesController implements QuoteApi {

    private final QuoteService quoteService;

    @Override
    public Mono<ResponseEntity<QuoteRates>> getCurrentRate(ServerWebExchange exchange) {
        log.debug("Getting current quote rate");

        return ReactiveAdapter.singleToMono(quoteService.getCurrentRateRx())
                .map(ResponseEntity::ok)
                .doOnSuccess(response -> log.info("Current rate retrieved successfully"))
                .doOnError(error -> log.error("Failed to get current rate: {}", error.getMessage()));
    }

    @Override
    public Mono<ResponseEntity<QuoteAccepted>> setQuoteRate(
            @Valid Mono<QuoteRates> quoteRates,
            ServerWebExchange exchange) {

        String correlationId = CorrelationHelper.extractOrGenerate(exchange);
        log.debug("Setting quote rate with correlation: {}", correlationId);

        return quoteRates
                .flatMap(rates -> processRateUpdate(rates, correlationId))
                .doOnSuccess(response -> log.info("Quote rate updated successfully: correlation={}", correlationId))
                .doOnError(error -> log.error("Failed to set quote rate for correlation {}: {}",
                        correlationId, error.getMessage()));
    }

    private Mono<ResponseEntity<QuoteAccepted>> processRateUpdate(QuoteRates rates, String correlationId) {
        return ReactiveAdapter.singleToMono(quoteService.setRatesRx(rates, correlationId))
                .map(accepted -> ResponseHelper.buildAcceptedWithCorrelation(accepted, correlationId));
    }
}