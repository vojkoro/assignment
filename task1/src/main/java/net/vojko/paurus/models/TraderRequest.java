package net.vojko.paurus.models;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import net.vojko.paurus.annotations.ValidTraderRequest;
import net.vojko.paurus.entities.TaxationMethodEnum;
import net.vojko.paurus.entities.TaxationTypeEnum;

@ValidTraderRequest
public record TraderRequest(@NotNull Long traderId, @NotNull TaxationTypeEnum taxationType,
        @NotNull TaxationMethodEnum taxationMethod, BigDecimal taxationRate, BigDecimal taxationAmount) {
}
