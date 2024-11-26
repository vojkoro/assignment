package net.vojko.paurus.models;

import java.math.BigDecimal;

public record TaxationRequest(Long traderId, BigDecimal playedAmount, BigDecimal odd) {
}
