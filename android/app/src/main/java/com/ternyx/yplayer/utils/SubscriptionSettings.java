package com.ternyx.yplayer.utils;

import java.time.Instant;
import java.time.Period;
import java.time.temporal.TemporalAmount;
import java.util.Date;

public class SubscriptionSettings {
    private Integer categoryId = null; // null denotes no category
    private Date startDate;
    private Date endDate;

    public SubscriptionSettings() {
        Instant instant = Instant.now();
        startDate = Date.from(instant);
        endDate = Date.from(instant.minus(Period.ofDays(30)));
    }
}
