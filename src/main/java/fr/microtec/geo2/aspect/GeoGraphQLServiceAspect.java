package fr.microtec.geo2.aspect;

import static fr.microtec.geo2.persistance.entity.FunctionResult.RESULT_UNKNOWN;

import java.time.Duration;
import java.util.List;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import com.zaxxer.hikari.HikariDataSource;

import fr.microtec.geo2.persistance.entity.FunctionResult;
import fr.microtec.geo2.service.security.SecurityService;
import graphql.GraphQLException;
import lombok.extern.slf4j.Slf4j;

/**
 * Aspect for all function graphql service.
 * Inspect FunctionResult return value and check if res is RESULT_UNKNOWN.
 * If it, throw GraphQLException for frontend.
 */
@Aspect
@Component
@Slf4j
public class GeoGraphQLServiceAspect {

    private final SecurityService securityService;
    private final HikariDataSource datasource;

    public GeoGraphQLServiceAspect(SecurityService securityService, HikariDataSource datasource) {
        this.securityService = securityService;
        this.datasource = datasource;
    }

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

    @Around(value = "execution(* fr.microtec.geo2.service.graphql..*(..))")
    public Object benchmarkAPI(ProceedingJoinPoint pjp) throws Throwable {
        String className = pjp.getSignature().getDeclaringType().getSimpleName();
        String methodName = pjp.getSignature().getName();
        String callArgs = List.of(pjp.getArgs()).toString();
        StopWatch stopWatch = new StopWatch(className);

        stopWatch.start(methodName);
        Object result = pjp.proceed();
        stopWatch.stop();

        String user = this.securityService.getUser().getNomUtilisateur();
        String request = String.format("%s.%s(%s)", className, methodName, callArgs);
        long duration = Duration.ofMillis(stopWatch.getLastTaskTimeMillis()).toSeconds();
        int activeDbConnections = this.datasource.getHikariPoolMXBean().getActiveConnections();

        log.info(String.format(" %d  %s  %dsec  %s  %s",
                activeDbConnections, user, duration, request, result));

        return result;
    }

}
