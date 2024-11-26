package net.vojko.paurus.interceptors;

import jakarta.annotation.Priority;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import jakarta.ws.rs.BadRequestException;
import java.math.BigDecimal;
import net.vojko.paurus.annotations.PositiveReturnOnly;

@PositiveReturnOnly
@Interceptor
@Priority(Interceptor.Priority.APPLICATION)
public class CheckNegativeReturnInterceptor {

    @AroundInvoke
    public Object checkReturnValue(InvocationContext context) throws Exception {
        Object result = context.proceed();
        if (result instanceof BigDecimal && ((BigDecimal) result).compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("Negative result where positive is expected");
        }
        return result;
    }
}
