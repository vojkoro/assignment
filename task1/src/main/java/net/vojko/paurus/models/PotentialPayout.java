package net.vojko.paurus.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PotentialPayout(BigDecimal possibleReturnAmount, BigDecimal possibleReturnAmountBefTax,
        BigDecimal possibleReturnAmountAfterTax, BigDecimal taxRate, BigDecimal taxAmount) {
}
