package net.vojko.paurus.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import net.vojko.paurus.entities.TaxationMethodEnum;
import net.vojko.paurus.entities.TaxationTypeEnum;
import net.vojko.paurus.entities.Trader;

@ApplicationScoped
public class TraderRepository implements PanacheRepository<Trader> {

    public Optional<Trader> getByTraderId(Long traderId) {
        return find("traderId", traderId).firstResultOptional();
    }

    public void deleteByTraderId(Long traderId) {
        delete("traderId", traderId);
    }

    public List<Trader> findAllWithValidEnums() {
        List<String> taxationTypeStrings = Arrays.stream(TaxationTypeEnum.values()).map(Enum::name)
                .collect(Collectors.toList());

        List<String> taxationMethodStrings = Arrays.stream(TaxationMethodEnum.values()).map(Enum::name)
                .collect(Collectors.toList());

        return find("CAST(taxationType as string) IN (?1) AND cast(taxationMethod as string) IN (?2)",
                taxationTypeStrings, taxationMethodStrings).list();
    }

    public List<Long> findAllWithInvalidEnums() {
        List<String> taxationTypeStrings = Arrays.stream(TaxationTypeEnum.values()).map(Enum::name)
                .collect(Collectors.toList());

        List<String> taxationMethodStrings = Arrays.stream(TaxationMethodEnum.values()).map(Enum::name)
                .collect(Collectors.toList());

        return getEntityManager().createQuery(
                "SELECT traderId FROM trader WHERE CAST(taxationType as string) NOT IN :taxationTypes OR CAST(taxationMethod as string) NOT IN :taxationMethods",
                Long.class).setParameter("taxationTypes", taxationTypeStrings)
                .setParameter("taxationMethods", taxationMethodStrings).getResultList();
    }

}
