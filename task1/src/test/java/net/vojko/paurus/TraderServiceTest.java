package net.vojko.paurus;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.validation.ValidationException;
import jakarta.ws.rs.NotFoundException;
import java.math.BigDecimal;
import net.vojko.paurus.entities.TaxationMethodEnum;
import net.vojko.paurus.entities.TaxationTypeEnum;
import net.vojko.paurus.models.TraderRequest;
import net.vojko.paurus.services.TraderService;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class TraderServiceTest {

    @Inject
    TraderService traderService;

    @Test
    public void createValidTrader() {
        var traderRequest = new TraderRequest(8L, TaxationTypeEnum.GENERAL, TaxationMethodEnum.RATE,
                BigDecimal.valueOf(0.1), null);
        var createdTrader = traderService.createTrader(traderRequest);
        var traderByTraderId = traderService.getTraderById(8L);
        assertThat(createdTrader, is(traderByTraderId));
        traderService.deleteTrader(8L);
    }

    @Test
    public void createInvalidTrader() {
        var traderRequest = new TraderRequest(11L, TaxationTypeEnum.GENERAL, TaxationMethodEnum.AMOUNT,
                BigDecimal.valueOf(0.1), null);

        assertThrows(ValidationException.class, () -> traderService.createTrader(traderRequest));

    }

    @Test
    public void getUnknownTrader() {
        assertThrows(NotFoundException.class, () -> traderService.getTraderById(99L));

    }
}
