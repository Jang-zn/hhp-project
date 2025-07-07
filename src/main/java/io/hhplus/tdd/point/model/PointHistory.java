package io.hhplus.tdd.point.model;

import io.hhplus.tdd.common.constants.TransactionType;

public record PointHistory(
        long id,
        long userId,
        long amount,
        TransactionType type,
        long updateMillis
) {
}
