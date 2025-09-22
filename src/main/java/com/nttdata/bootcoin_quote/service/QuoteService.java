package com.nttdata.bootcoin_quote.service;

import com.nttdata.model.QuoteAccepted;
import com.nttdata.model.QuoteRates;
import io.reactivex.rxjava3.core.Single;


public interface QuoteService {

    Single<QuoteRates> getCurrentRateRx();

    Single<QuoteAccepted> setRatesRx(QuoteRates rates, String correlationId);
}