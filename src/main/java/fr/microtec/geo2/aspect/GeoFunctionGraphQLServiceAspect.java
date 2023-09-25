package fr.microtec.geo2.aspect;

import fr.microtec.geo2.persistance.entity.FunctionResult;
import graphql.GraphQLException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import static fr.microtec.geo2.persistance.entity.FunctionResult.RESULT_UNKNOWN;

/**
 * Aspect for all function graphql service.
 * Inspect FunctionResult return value and check if res is RESULT_UNKNOWN.
 * If it, throw GraphQLException for frontend.
 */
@Aspect
@Component
public class GeoFunctionGraphQLServiceAspect {

    @Pointcut("execution(public fr.microtec.geo2.persistance.entity.FunctionResult fr.microtec.geo2.service.graphql..*(..))")
    public void serviceMethods() {
    }

    @AfterReturning(value = "serviceMethods()", returning = "result")
    public void logMethodCall(JoinPoint jp, FunctionResult result) {
        if (result.getRes() == RESULT_UNKNOWN) {
            String message = result.getMsg();
            throw new GraphQLException(message != null ? message
                    : "Une erreur est survenue pendant l'execution de la procédure");
        }
    }

}
