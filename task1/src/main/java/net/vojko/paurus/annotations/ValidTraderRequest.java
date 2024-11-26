package net.vojko.paurus.annotations;

import static java.lang.annotation.ElementType.*;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import net.vojko.paurus.validators.TraderRequestValidator;

@Documented
@Constraint(validatedBy = TraderRequestValidator.class)
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidTraderRequest {
    String message() default "Invalid trader configuration";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
