package com.nttdata.bootcoin_quote.service.helper;

import lombok.experimental.UtilityClass;

import java.time.Duration;

@UtilityClass
public class QuoteConstants {

    public static final String REDIS_LAST_RATE_KEY = "bootcoin:quote:last";
    public static final String REDIS_CID_KEY_PREFIX = "bootcoin:quote:cid:";

    public static final Duration CID_TTL = Duration.ofMinutes(10);

    public static final String HDR_EVENT_TYPE = "eventType";
    public static final String HDR_EVENT_VERSION = "eventVersion";
    public static final String HDR_EVENT_ID = "eventId";
    public static final String HDR_CORRELATION_ID = "correlationId";

    public static final String EVT_TYPE_QUOTE_UPDATED = "bootcoin.quote.updated";
}