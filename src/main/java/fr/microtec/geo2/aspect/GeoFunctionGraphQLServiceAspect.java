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
 * Inspect FunctionResult return value and check if res is 0.
 * If it, throw GraphQLException for frontend.
 */
@Aspect
@Component
public class GeoFunctionGraphQLServiceAspect {

    @Pointcut("execution(public fr.microtec.geo2.persistance.entity.FunctionResult fr.microtec.geo2.service.graphql..*(..))")
    public void serviceMethods() {}

    @AfterReturning(value = "serviceMethods()", returning = "result")
    public void logMethodCall(JoinPoint jp, FunctionResult result) {
        if (result.getRes() == 0) {
            throw new GraphQLException(result.getMsg());
        }
    }

}
