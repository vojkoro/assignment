package net.vojko.paurus.annotations;

import java.lang.annotation.*;
import net.vojko.paurus.entities.TaxationMethodEnum;
import net.vojko.paurus.entities.TaxationTypeEnum;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD, ElementType.FIELD })
@Documented
@Inherited
public @interface TaxStrategy {
    TaxationTypeEnum type();

    TaxationMethodEnum method();
}
