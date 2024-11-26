package net.vojko.paurus.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

@Entity(name = "trader")
public class Trader extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "trader_id", nullable = false, updatable = false)
    private Long traderId;

    @Column(name = "taxation_type")
    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private TaxationTypeEnum taxationType;

    @Column(name = "taxation_method")
    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private TaxationMethodEnum taxationMethod;

    @Column(name = "taxation_rate")
    private BigDecimal taxationRate;

    @Column(name = "taxation_amount")
    private BigDecimal taxationAmount;

    public Trader() {
    }

    public Long getTraderId() {
        return traderId;
    }

    public void setTraderId(Long traderId) {
        this.traderId = traderId;
    }

    public TaxationTypeEnum getTaxationType() {
        return taxationType;
    }

    public void setTaxationType(TaxationTypeEnum taxationType) {
        this.taxationType = taxationType;
    }

    public TaxationMethodEnum getTaxationMethod() {
        return taxationMethod;
    }

    public void setTaxationMethod(TaxationMethodEnum taxationMethod) {
        this.taxationMethod = taxationMethod;
    }

    public BigDecimal getTaxationRate() {
        return taxationRate;
    }

    public void setTaxationRate(BigDecimal taxationRate) {
        this.taxationRate = taxationRate;
    }

    public BigDecimal getTaxationAmount() {
        return taxationAmount;
    }

    public void setTaxationAmount(BigDecimal taxationAmount) {
        this.taxationAmount = taxationAmount;
    }

    public Long getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Trader trader = (Trader) o;
        return Objects.equals(id, trader.id) && Objects.equals(traderId, trader.traderId)
                && taxationType == trader.taxationType && taxationMethod == trader.taxationMethod
                && Objects.equals(taxationRate, trader.taxationRate)
                && Objects.equals(taxationAmount, trader.taxationAmount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, traderId, taxationType, taxationMethod, taxationRate, taxationAmount);
    }
}
