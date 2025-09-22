package com.nttdata.bootcoin_quote.service.helper;

import com.nttdata.bootcoin_quote.events.QuoteUpdatedEvent;
import com.nttdata.model.QuoteRates;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.time.Instant;

import static com.nttdata.bootcoin_quote.service.helper.QuoteConstants.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class QuoteEventHelper {

    private final KafkaTemplate<String, QuoteUpdatedEvent> kafka;

    @Value("${app.topics.quote-updated:quote.updated}")
    private String topic;

    public Single<Boolean> publishQuoteUpdatedEvent(QuoteRates rates, String eventId, 
                                                   String correlationId, Instant occurredAt) {
        QuoteUpdatedEvent payload = new QuoteUpdatedEvent(
                rates.getBuyRate(), 
                rates.getSellRate(), 
                occurredAt
        );

        Message<QuoteUpdatedEvent> message = MessageBuilder
                .withPayload(payload)
                .setHeader(KafkaHeaders.TOPIC, topic)
                .setHeader(HDR_EVENT_TYPE, EVT_TYPE_QUOTE_UPDATED)
                .setHeader(HDR_EVENT_VERSION, "1")
                .setHeader(HDR_EVENT_ID, eventId)
                .setHeader(HDR_CORRELATION_ID, correlationId)
                .build();

        return Single.fromCompletionStage(kafka.send(message).completable())
                .map(sendResult -> Boolean.TRUE)
                .doOnSuccess(result -> log.info("Quote updated event published: {} for correlation: {}", 
                        eventId, correlationId))
                .doOnError(error -> log.error("Failed to publish quote event for correlation {}: {}", 
                        correlationId, error.getMessage()));
    }
}