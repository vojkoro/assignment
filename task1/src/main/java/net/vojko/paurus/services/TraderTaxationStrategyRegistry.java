package net.vojko.paurus.services;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import java.util.HashMap;
import java.util.Map;
import net.vojko.paurus.annotations.TaxStrategy;
import net.vojko.paurus.entities.TaxationMethodEnum;
import net.vojko.paurus.entities.TaxationTypeEnum;
import net.vojko.paurus.entities.Trader;
import net.vojko.paurus.strategies.TaxCalculationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class TraderTaxationStrategyRegistry {
    private static final Logger log = LoggerFactory.getLogger(TraderTaxationStrategyRegistry.class);
    private final Map<String, TaxCalculationStrategy> strategyMap = new HashMap<>();
    private final Map<Long, Trader> traderInfoMap = new HashMap<>();
    private final Map<Long, TaxCalculationStrategy> traderStrategyMap = new HashMap<>();
    @Inject
    Instance<TaxCalculationStrategy> strategies;
    @Inject
    TraderService traderService;

    void onStart(@Observes StartupEvent ev) {
        init();
    }

    void init() {
        strategyMap.clear();
        traderInfoMap.clear();
        traderStrategyMap.clear();
        registerStrategies();
        checkForInvalidTraderTaxations();
        pairTradersWithStrategies();
    }

    private void pairTradersWithStrategies() {
        traderService.findAllWithValidEnums().forEach(trader -> {
            traderInfoMap.put(trader.getTraderId(), trader);
            String key = generateKey(trader.getTaxationType(), trader.getTaxationMethod());
            TaxCalculationStrategy strategy = strategyMap.get(key);
            if (strategy == null) {
                log.warn("No appropriate strategy found for trader: {}", trader.getTraderId());
                return;
            }
            setStrategyProperties(trader, strategy);
        });
    }

    private void setStrategyProperties(Trader trader, TaxCalculationStrategy strategy) {
        try {
            strategy.setRate(trader.getTaxationRate());
            strategy.setAmount(trader.getTaxationAmount());
            traderStrategyMap.put(trader.getTraderId(), strategy);
        } catch (Exception e) {
            log.error("Problem while setting trader strategy: {}", trader.getTraderId());
        }
    }

    private void checkForInvalidTraderTaxations() {
        var invalidTraders = traderService.findAllWithInvalidEnums();
        invalidTraders.forEach(traderId -> log.warn("Invalid trader config found in database: {}", traderId));
    }

    private void registerStrategies() {
        for (TaxCalculationStrategy strategy : strategies) {
            TaxStrategy taxStrategyAnnotation = strategy.getClass().getAnnotation(TaxStrategy.class);
            if (taxStrategyAnnotation != null) {
                var strategyType = taxStrategyAnnotation.type();
                var strategyMethod = taxStrategyAnnotation.method();
                strategyMap.put(generateKey(strategyType, strategyMethod), strategy);
            }
        }
    }

    public TaxCalculationStrategy getStrategyForTrader(Long traderId) {
        Trader trader = traderInfoMap.get(traderId);
        if (trader == null) {
            ;
            throw new NotFoundException("No trader found or wrong configuration for traderId: " + traderId);
        }
        TaxCalculationStrategy strategy = traderStrategyMap.get(traderId);
        if (strategy == null) {
            throw new NotFoundException("No strategy found for trader: " + traderId);
        }
        return strategy;
    }

    private String generateKey(TaxationTypeEnum type, TaxationMethodEnum method) {
        return type + ":" + method;
    }
}
