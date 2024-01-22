package fr.microtec.geo2.aspect;

import static fr.microtec.geo2.persistance.entity.FunctionResult.RESULT_UNKNOWN;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import fr.microtec.geo2.persistance.entity.FunctionResult;
import graphql.GraphQLException;

/**
 * Aspect for all function graphql service.
 * Inspect FunctionResult return value and check if res is RESULT_UNKNOWN.
 * If it, throw GraphQLException for frontend.
 */
@Aspect
@Component
public class GeoGraphQLFunctionServiceAspect {

    @Pointcut("execution(public fr.microtec.geo2.persistance.entity.FunctionResult fr.microtec.geo2.service.graphql..*(..))")
    public void serviceMethods() {
    }

    @AfterReturning(value = "serviceMethods()", returning = "result")
    public void catchUnknown(JoinPoint jp, FunctionResult result) {
        if (result.getRes() == RESULT_UNKNOWN) {
            String message = result.getMsg();
            throw new GraphQLException(message != null ? message
                    : "Une erreur est survenue pendant l'execution de la procédure");
        }
    }

}
