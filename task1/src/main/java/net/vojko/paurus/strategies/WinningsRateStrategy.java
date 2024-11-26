package net.vojko.paurus.strategies;

import jakarta.enterprise.context.ApplicationScoped;
import java.math.BigDecimal;
import net.vojko.paurus.annotations.PositiveReturnOnly;
import net.vojko.paurus.annotations.TaxStrategy;
import net.vojko.paurus.entities.TaxationMethodEnum;
import net.vojko.paurus.entities.TaxationTypeEnum;

@ApplicationScoped
@TaxStrategy(type = TaxationTypeEnum.WINNING, method = TaxationMethodEnum.RATE)
public class WinningsRateStrategy extends TaxCalculationBase {

    public BigDecimal calculatePossibleReturnAfterTax(BigDecimal payedAmount, BigDecimal odd) {
        if (getRate() == null)
            throw new IllegalStateException("Rate is not set");
        var general = calculatePossibleReturnBeforeTax(payedAmount, odd);
        var winning = general.subtract(payedAmount);
        var amountAfterTax = amountAfterTax(general, winning);
        return amountAfterTax;
    }

    @PositiveReturnOnly
    public BigDecimal calculatePossibleReturnBeforeTax(BigDecimal payedAmount, BigDecimal odd) {
        return payedAmount.multiply(odd);
    }

    @PositiveReturnOnly
    public BigDecimal amountAfterTax(BigDecimal general, BigDecimal winning) {
        return general.subtract(winning.multiply(getRate()));
    }

}
