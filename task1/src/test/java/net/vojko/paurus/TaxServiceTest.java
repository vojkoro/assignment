package net.vojko.paurus;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectSpy;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import java.math.BigDecimal;
import java.util.stream.Stream;
import net.vojko.paurus.entities.TaxationMethodEnum;
import net.vojko.paurus.entities.TaxationTypeEnum;
import net.vojko.paurus.models.Bet;
import net.vojko.paurus.models.PotentialPayout;
import net.vojko.paurus.services.TaxService;
import net.vojko.paurus.services.TraderService;
import net.vojko.paurus.services.TraderTaxationStrategyRegistry;
import net.vojko.paurus.strategies.GeneralAmountStrategy;
import net.vojko.paurus.strategies.GeneralRateStrategy;
import net.vojko.paurus.strategies.WinningsAmountStrategy;
import net.vojko.paurus.strategies.WinningsRateStrategy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

@QuarkusTest
class TaxServiceTest {

    @InjectSpy
    TraderTaxationStrategyRegistry registry;

    @Inject
    TaxService taxService;

    @Inject
    TraderService traderService;

    @ParameterizedTest
    @MethodSource("tradersWithStrategies")
    void calculateTaxationForTrader(Long traderId, PotentialPayout expected, TaxationTypeEnum taxationType,
            TaxationMethodEnum taxationMethod) {
        Bet bet = new Bet(new BigDecimal("5"), new BigDecimal("1.5"));
        var trader = traderService.getTraderById(traderId);
        assertThat(trader.taxationType(), is(taxationType));
        assertThat(trader.taxationMethod(), is(taxationMethod));
        var potentialPayout = taxService.calculateTax(trader.traderId(), bet);
        assertThat(potentialPayout, is(expected));
    }

    @ParameterizedTest
    @ValueSource(longs = { 5, 6 })
    void missingTaxation(Long traderId) {
        assertThrows(NotFoundException.class,
                () -> taxService.calculateTax(traderId, new Bet(new BigDecimal("5"), new BigDecimal("1.5"))));
    }

    public static Stream<Arguments> tradersWithStrategies() {
        return Stream.of(
                Arguments.of(1L,
                        new PotentialPayout(new BigDecimal("6.75"), new BigDecimal("7.5"), new BigDecimal("6.75"),
                                new BigDecimal("0.1"), null),
                        TaxationTypeEnum.GENERAL, TaxationMethodEnum.RATE),
                Arguments.of(2L,
                        new PotentialPayout(new BigDecimal("5.5"), new BigDecimal("7.5"), new BigDecimal("5.5"), null,
                                new BigDecimal("2")),
                        TaxationTypeEnum.GENERAL, TaxationMethodEnum.AMOUNT),
                Arguments.of(3L,
                        new PotentialPayout(new BigDecimal("7.25"), new BigDecimal("7.5"), new BigDecimal("7.25"),
                                new BigDecimal("0.1"), null),
                        TaxationTypeEnum.WINNING, TaxationMethodEnum.RATE),
                Arguments.of(4L, new PotentialPayout(new BigDecimal("6.0"), new BigDecimal("7.5"),
                        new BigDecimal("6.0"), null, new BigDecimal("1")), TaxationTypeEnum.WINNING,
                        TaxationMethodEnum.AMOUNT));
    }

    @Test
    void testStrategy_withGeneralRate() {
        GeneralRateStrategy mockStrategy = new GeneralRateStrategy();
        mockStrategy.setRate(new BigDecimal("0.1"));
        doReturn(mockStrategy).when(registry).getStrategyForTrader(1L);

        var expectedResult = new PotentialPayout(new BigDecimal("6.75"), new BigDecimal("7.5"), new BigDecimal("6.75"),
                new BigDecimal("0.1"), null);
        var tax1 = taxService.calculateTax(1L, new Bet(new BigDecimal("5"), new BigDecimal("1.5")));

        assertThat(tax1, is(expectedResult));
    }

    @Test
    void testStrategy_withGeneralAmount() {
        GeneralAmountStrategy mockStrategy = new GeneralAmountStrategy();
        mockStrategy.setAmount(new BigDecimal("2"));
        doReturn(mockStrategy).when(registry).getStrategyForTrader(1L);

        var expectedResult = new PotentialPayout(new BigDecimal("5.5"), new BigDecimal("7.5"), new BigDecimal("5.5"),
                null, new BigDecimal("2"));
        var tax1 = taxService.calculateTax(1L, new Bet(new BigDecimal("5"), new BigDecimal("1.5")));

        assertThat(tax1, is(expectedResult));
    }

    @Test
    void testStrategy_withWinningRate() {
        WinningsRateStrategy mockStrategy = new WinningsRateStrategy();
        mockStrategy.setRate(new BigDecimal("0.1"));
        doReturn(mockStrategy).when(registry).getStrategyForTrader(1L);

        var expectedResult = new PotentialPayout(new BigDecimal("7.25"), new BigDecimal("7.5"), new BigDecimal("7.25"),
                new BigDecimal("0.1"), null);
        var tax1 = taxService.calculateTax(1L, new Bet(new BigDecimal("5"), new BigDecimal("1.5")));

        assertThat(tax1, is(expectedResult));
    }

    @Test
    void testStrategy_withWinningAmount() {
        WinningsAmountStrategy mockStrategy = new WinningsAmountStrategy();
        mockStrategy.setAmount(new BigDecimal("1"));
        doReturn(mockStrategy).when(registry).getStrategyForTrader(1L);

        var expectedResult = new PotentialPayout(new BigDecimal("6.0"), new BigDecimal("7.5"), new BigDecimal("6.0"),
                null, new BigDecimal(1));
        var tax1 = taxService.calculateTax(1L, new Bet(new BigDecimal("5"), new BigDecimal("1.5")));

        assertThat(tax1, is(expectedResult));
    }
}
