package com.nttdata.bootcoin_quote.service.impl;

import com.nttdata.bootcoin_quote.service.QuoteService;
import com.nttdata.bootcoin_quote.service.helper.QuoteEventHelper;
import com.nttdata.bootcoin_quote.service.helper.QuoteValidationHelper;
import com.nttdata.bootcoin_quote.api.ResourceNotFoundException;
import com.nttdata.model.QuoteAccepted;
import com.nttdata.model.QuoteRates;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import static com.nttdata.bootcoin_quote.service.helper.QuoteConstants.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuoteServiceImpl implements QuoteService {

    private final ReactiveRedisTemplate<String, QuoteRates> redis;
    private final QuoteEventHelper eventHelper;

    @Override
    public Single<QuoteRates> getCurrentRateRx() {
        log.debug("Getting current quote rate from Redis");
        
        return Maybe.fromPublisher(redis.opsForValue().get(REDIS_LAST_RATE_KEY))
                .switchIfEmpty(Maybe.error(new ResourceNotFoundException("No hay cotización cargada aún")))
                .toSingle()
                .doOnSuccess(rates -> log.info("Current rate retrieved: buy={}, sell={}", 
                        rates.getBuyRate(), rates.getSellRate()))
                .doOnError(error -> log.error("Failed to get current rate: {}", error.getMessage()));
    }

    @Override
    public Single<QuoteAccepted> setRatesRx(QuoteRates rates, String correlationId) {
        log.debug("Setting new rates with correlation: {}", correlationId);
        
        return QuoteValidationHelper.validateRates(rates)
                .flatMap(validRates -> QuoteValidationHelper.validateCorrelationId(correlationId)
                        .map(validCorrelationId -> validRates))
                .flatMap(validRates -> processRatesUpdate(validRates, correlationId))
                .doOnSuccess(accepted -> log.info("Rates updated successfully: buy={}, sell={}, correlation={}", 
                        accepted.getBuyRate(), accepted.getSellRate(), correlationId))
                .doOnError(error -> log.error("Failed to set rates for correlation {}: {}", 
                        correlationId, error.getMessage()));
    }

    // ========== PRIVATE HELPERS ==========

    private Single<QuoteAccepted> processRatesUpdate(QuoteRates rates, String correlationId) {
        final String cidKey = REDIS_CID_KEY_PREFIX + correlationId;
        final String eventId = UUID.randomUUID().toString();
        final Instant occurredAtInstant = Instant.now();
        final OffsetDateTime occurredAt = OffsetDateTime.ofInstant(occurredAtInstant, ZoneOffset.UTC);

        return checkDuplicateCorrelationId(cidKey, rates)
                .flatMap(__ -> saveLastRate(rates))
                .flatMap(__ -> eventHelper.publishQuoteUpdatedEvent(rates, eventId, correlationId, occurredAtInstant))
                .map(__ -> buildQuoteAccepted(rates, correlationId, occurredAt));
    }

    private Single<Boolean> checkDuplicateCorrelationId(String cidKey, QuoteRates rates) {
        return Single.fromPublisher(redis.opsForValue().setIfAbsent(cidKey, rates, CID_TTL))
                .flatMap(wasSet -> wasSet 
                        ? Single.just(true)
                        : Single.error(new IllegalStateException("Duplicate correlationId")));
    }

    private Single<Boolean> saveLastRate(QuoteRates rates) {
        return Single.fromPublisher(redis.opsForValue().set(REDIS_LAST_RATE_KEY, rates));
    }

    private QuoteAccepted buildQuoteAccepted(QuoteRates rates, String correlationId, OffsetDateTime occurredAt) {
        return new QuoteAccepted()
                .buyRate(rates.getBuyRate())
                .sellRate(rates.getSellRate())
                .occurredAt(occurredAt)
                .correlationId(correlationId);
    }
}