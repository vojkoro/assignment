package net.vojko.paurus.mappers;

import net.vojko.paurus.entities.Trader;
import net.vojko.paurus.models.TraderRequest;
import net.vojko.paurus.models.TraderResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TraderMapper {
    TraderMapper INSTANCE = Mappers.getMapper(TraderMapper.class);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "traderId", target = "traderId")
    @Mapping(source = "taxationType", target = "taxationType")
    @Mapping(source = "taxationMethod", target = "taxationMethod")
    @Mapping(source = "taxationRate", target = "taxationRate")
    @Mapping(source = "taxationAmount", target = "taxationAmount")
    TraderResponse traderToTraderResponse(Trader trader);

    @Mapping(source = "traderId", target = "traderId")
    @Mapping(source = "taxationType", target = "taxationType")
    @Mapping(source = "taxationMethod", target = "taxationMethod")
    @Mapping(source = "taxationRate", target = "taxationRate")
    @Mapping(source = "taxationAmount", target = "taxationAmount")
    Trader traderRequestToTrader(TraderRequest traderRequest);

}