package net.vojko.paurus.strategies;

import jakarta.enterprise.context.ApplicationScoped;
import java.math.BigDecimal;
import net.vojko.paurus.annotations.PositiveReturnOnly;
import net.vojko.paurus.annotations.TaxStrategy;
import net.vojko.paurus.entities.TaxationMethodEnum;
import net.vojko.paurus.entities.TaxationTypeEnum;

@ApplicationScoped
@TaxStrategy(type = TaxationTypeEnum.GENERAL, method = TaxationMethodEnum.AMOUNT)
public class GeneralAmountStrategy extends TaxCalculationBase implements TaxCalculationStrategy {

    public BigDecimal calculatePossibleReturnAfterTax(BigDecimal payedAmount, BigDecimal odd) {
        if (getAmount() == null)
            throw new IllegalStateException("Amount is not set");
        var general = calculatePossibleReturnBeforeTax(payedAmount, odd);
        var amountAfterTax = amountAfterTax(general);
        return amountAfterTax;
    }

    @PositiveReturnOnly
    public BigDecimal calculatePossibleReturnBeforeTax(BigDecimal payedAmount, BigDecimal odd) {
        return payedAmount.multiply(odd);
    }

    @PositiveReturnOnly
    public BigDecimal amountAfterTax(BigDecimal general) {
        return general.subtract(getAmount());
    }

}