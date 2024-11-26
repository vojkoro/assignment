package net.vojko.paurus.strategies;

import java.math.BigDecimal;
import net.vojko.paurus.annotations.PositiveReturnOnly;

public abstract class TaxCalculationBase implements TaxCalculationStrategy {
    private BigDecimal amount;
    private BigDecimal rate;

    @PositiveReturnOnly
    public BigDecimal getAmount() {
        return amount;
    }

    @PositiveReturnOnly
    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

}
