package net.vojko.paurus.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import net.vojko.paurus.annotations.ValidTraderRequest;
import net.vojko.paurus.entities.TaxationMethodEnum;
import net.vojko.paurus.models.TraderRequest;

public class TraderRequestValidator implements ConstraintValidator<ValidTraderRequest, TraderRequest> {

    @Override
    public boolean isValid(TraderRequest traderRequest, ConstraintValidatorContext constraintValidatorContext) {
        return validateTraderConfig(traderRequest, constraintValidatorContext);
    }

    private boolean validateTraderConfig(TraderRequest traderRequest, ConstraintValidatorContext context) {
        if (traderRequest.taxationMethod() == TaxationMethodEnum.AMOUNT && traderRequest.taxationAmount() == null) {
            addConstraintViolation(context, "Taxation amount is required for AMOUNT taxation method", "taxationAmount");
            return false;
        }
        if (traderRequest.taxationMethod() == TaxationMethodEnum.RATE && traderRequest.taxationRate() == null) {
            addConstraintViolation(context, "Taxation rate is required for RATE taxation method", "taxationRate");
            return false;
        }
        if (traderRequest.taxationMethod() == TaxationMethodEnum.RATE
                && traderRequest.taxationRate().compareTo(new java.math.BigDecimal(0)) < 0) {
            addConstraintViolation(context, "Taxation rate must be greater than 0", "taxationRate");
            return false;
        }
        if (traderRequest.taxationMethod() == TaxationMethodEnum.AMOUNT
                && traderRequest.taxationAmount().compareTo(new java.math.BigDecimal(0)) < 0) {
            addConstraintViolation(context, "Taxation amount must be greater than 0", "taxationAmount");
            return false;
        }
        return true;
    }

    private static void addConstraintViolation(ConstraintValidatorContext context, String messageTemplate,
            String property) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(messageTemplate).addPropertyNode(property)
                .addConstraintViolation();

    }
}
