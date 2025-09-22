package com.nttdata.bootcoin_quote.events;

import java.math.BigDecimal;
import java.time.Instant;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuoteUpdatedEvent {
    private BigDecimal buyRate;
    private BigDecimal sellRate;
    private Instant occurredAt;
}
