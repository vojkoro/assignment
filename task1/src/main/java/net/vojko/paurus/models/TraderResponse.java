package net.vojko.paurus.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import net.vojko.paurus.entities.TaxationMethodEnum;
import net.vojko.paurus.entities.TaxationTypeEnum;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TraderResponse(@NotNull Long id, @NotNull Long traderId, @NotNull TaxationTypeEnum taxationType,
        @NotNull TaxationMethodEnum taxationMethod, BigDecimal taxationRate, BigDecimal taxationAmount) {
}
