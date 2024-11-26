package net.vojko.paurus.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import net.vojko.paurus.models.Bet;
import net.vojko.paurus.models.PotentialPayout;
import net.vojko.paurus.strategies.TaxCalculationStrategy;

@ApplicationScoped
public class TaxService {

    @Inject
    TraderTaxationStrategyRegistry registry;

    public PotentialPayout calculateTax(Long traderId, Bet bet) {
        TaxCalculationStrategy strategy = registry.getStrategyForTrader(traderId);

        var possibleReturnAmount = strategy.calculatePossibleReturnAfterTax(bet.amount(), bet.odd());
        var possibleReturnAmountBeforeTax = strategy.calculatePossibleReturnBeforeTax(bet.amount(), bet.odd());
        var possibleReturnAmountAfterTax = strategy.calculatePossibleReturnAfterTax(bet.amount(), bet.odd());

        return new PotentialPayout(possibleReturnAmount, possibleReturnAmountBeforeTax, possibleReturnAmountAfterTax,
                strategy.getRate(), strategy.getAmount());
    }
}
