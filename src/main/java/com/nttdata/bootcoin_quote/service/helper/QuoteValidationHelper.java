package com.nttdata.bootcoin_quote.service.helper;

import com.nttdata.model.QuoteRates;
import io.reactivex.rxjava3.core.Single;
import lombok.experimental.UtilityClass;

import java.util.Objects;

@UtilityClass
public class QuoteValidationHelper {

    public Single<QuoteRates> validateRates(QuoteRates rates) {
        if (rates == null) {
            return Single.error(new NullPointerException("rates is required"));
        }
        
        if (rates.getBuyRate() == null || rates.getSellRate() == null) {
            return Single.error(new IllegalArgumentException("buyRate and sellRate are required"));
        }
        
        if (rates.getBuyRate().doubleValue() <= 0) {
            return Single.error(new IllegalArgumentException("buyRate must be > 0"));
        }
        
        if (rates.getSellRate().doubleValue() <= 0) {
            return Single.error(new IllegalArgumentException("sellRate must be > 0"));
        }
        
        return Single.just(rates);
    }

    public Single<String> validateCorrelationId(String correlationId) {
        if (correlationId == null || correlationId.isBlank()) {
            return Single.error(new IllegalArgumentException("Missing X-Correlation-Id"));
        }
        return Single.just(correlationId);
    }
}