package net.vojko.paurus.strategies;

import java.math.BigDecimal;

public interface TaxCalculationStrategy {
    BigDecimal calculatePossibleReturnAfterTax(BigDecimal amount, BigDecimal odd);

    BigDecimal calculatePossibleReturnBeforeTax(BigDecimal amount, BigDecimal odd);

    BigDecimal getAmount();

    void setAmount(BigDecimal amount);

    BigDecimal getRate();

    void setRate(BigDecimal rate);
}
