package net.vojko.paurus.strategies;

import jakarta.enterprise.context.ApplicationScoped;
import java.math.BigDecimal;
import net.vojko.paurus.annotations.PositiveReturnOnly;
import net.vojko.paurus.annotations.TaxStrategy;
import net.vojko.paurus.entities.TaxationMethodEnum;
import net.vojko.paurus.entities.TaxationTypeEnum;

@ApplicationScoped
@TaxStrategy(type = TaxationTypeEnum.WINNING, method = TaxationMethodEnum.AMOUNT)
public class WinningsAmountStrategy extends TaxCalculationBase implements TaxCalculationStrategy {

    public BigDecimal calculatePossibleReturnAfterTax(BigDecimal payedAmount, BigDecimal odd) {
        if (getAmount() == null)
            throw new IllegalStateException("Amount is not set");
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
        return general.subtract(winning.subtract(getAmount()));
    }
}
