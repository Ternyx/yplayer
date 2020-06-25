package com.github.ternyx.models;

import java.time.Instant;
import java.time.Period;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SubscriptionSettings {
    private int DEFAULT_END_DATE_TRESHOLD = 30;

    private Integer categoryId = null; // null denotes no category
    private Instant startDate;
    private Instant endDate;

    public SubscriptionSettings() {
        Instant instant = Instant.now();
        startDate = instant;
        endDate = instant.minus(Period.ofDays(DEFAULT_END_DATE_TRESHOLD));
    }

    public SubscriptionSettings(Integer categoryId, Instant startDate, Instant endDate) {
        this.categoryId = categoryId;
        this.startDate = startDate;
        this.endDate = endDate;
    }
    // TODO validations
}


