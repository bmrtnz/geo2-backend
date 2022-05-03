package fr.microtec.geo2.aspect;

import fr.microtec.geo2.persistance.entity.FunctionResult;
import graphql.GraphQLException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * Aspect for all function graphql service.
 * Inspect FunctionResult return value and check if res is RESULT_UNKNOWN.
 * If it, throw GraphQLException for frontend.
 */
@Aspect
@Component
public class GeoFunctionGraphQLServiceAspect {

    // Declared only for readability and comprehension
    private final static int RESULT_UNKNOWN = 0;
    private final static int RESULT_OK = 1;
    private final static int RESULT_WARNING = 2;

    @Pointcut("execution(public fr.microtec.geo2.persistance.entity.FunctionResult fr.microtec.geo2.service.graphql..*(..))")
    public void serviceMethods() {}

    @AfterReturning(value = "serviceMethods()", returning = "result")
    public void logMethodCall(JoinPoint jp, FunctionResult result) {
        if (result.getRes() == RESULT_UNKNOWN) {
            throw new GraphQLException(result.getMsg());
        }
    }

}
