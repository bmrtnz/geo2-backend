package fr.microtec.geo2.aspect;

import java.time.Duration;
import java.util.Optional;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import com.zaxxer.hikari.HikariDataSource;

import fr.microtec.geo2.service.security.SecurityService;
import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
@ConditionalOnProperty(value = "geo2.enable-api-benchmark")
public class GeoGraphQLServiceAspect {

    private final SecurityService securityService;
    private final HikariDataSource datasource;

    public GeoGraphQLServiceAspect(SecurityService securityService, HikariDataSource datasource) {
        this.securityService = securityService;
        this.datasource = datasource;
    }

    @Around(value = "execution(* fr.microtec.geo2.service.graphql..*(..))")
    public Object benchmarkAPI(ProceedingJoinPoint pjp) throws Throwable {
        String className = pjp.getSignature().getDeclaringType().getSimpleName();
        String methodName = pjp.getSignature().getName();
        String callArgs = Optional.ofNullable(pjp.getArgs()).orElse(new Object[0]).toString();
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
