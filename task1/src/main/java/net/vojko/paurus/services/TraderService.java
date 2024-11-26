package net.vojko.paurus.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import java.util.List;
import net.vojko.paurus.entities.Trader;
import net.vojko.paurus.mappers.TraderMapper;
import net.vojko.paurus.models.TraderRequest;
import net.vojko.paurus.models.TraderResponse;
import net.vojko.paurus.repositories.TraderRepository;

@ApplicationScoped
public class TraderService {

    private final TraderRepository traderRepository;

    TraderService(TraderRepository traderRepository) {
        this.traderRepository = traderRepository;
    }

    public TraderResponse getTraderById(Long traderId) {
        var trader = traderRepository.getByTraderId(traderId)
                .orElseThrow(() -> new NotFoundException("Trader not found"));
        return TraderMapper.INSTANCE.traderToTraderResponse(trader);
    }

    @Transactional
    public void deleteTrader(Long traderId) {
        traderRepository.deleteByTraderId(traderId);
    }

    @Transactional
    public TraderResponse createTrader(@Valid TraderRequest traderRequest) {
        var trader = TraderMapper.INSTANCE.traderRequestToTrader(traderRequest);
        if (traderRepository.getByTraderId(trader.getTraderId()).isPresent()) {
            throw new BadRequestException("Trader with id " + trader.getTraderId() + " already exists");
        }
        traderRepository.persist(trader);
        return TraderMapper.INSTANCE.traderToTraderResponse(trader);
    }

    @Transactional
    public TraderResponse updateTrader(Long traderId, @Valid TraderRequest traderRequest) {
        var fetchedTrader = traderRepository.getByTraderId(traderId)
                .orElseThrow(() -> new NotFoundException("Trader for update not found"));
        fetchedTrader.setTaxationType(traderRequest.taxationType());
        fetchedTrader.setTaxationMethod(traderRequest.taxationMethod());
        fetchedTrader.setTaxationRate(traderRequest.taxationRate());
        fetchedTrader.setTaxationAmount(traderRequest.taxationAmount());
        traderRepository.persist(fetchedTrader);
        return TraderMapper.INSTANCE.traderToTraderResponse(fetchedTrader);
    }

    public List<Trader> findAllWithValidEnums() {
        return traderRepository.findAllWithValidEnums();
    }

    public List<Long> findAllWithInvalidEnums() {
        return traderRepository.findAllWithInvalidEnums();
    }

}
