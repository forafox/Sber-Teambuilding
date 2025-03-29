package com.jellyone.configuration;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspectConfig {

    private static final Logger log = LoggerFactory.getLogger(LoggingAspectConfig.class);

    /**
     * Pointcut for methods annotated with @Loggable
     */
    @Pointcut("@annotation(com.jellyone.configuration.annotation.Loggable)")
    public void loggableMethods() {
    }

    /**
     * Pointcut that matches all repositories, services and Web REST endpoints.
     */
    @Pointcut(
            "within(@org.springframework.stereotype.Repository *) " +
                    "|| within(@org.springframework.stereotype.Service *) " +
                    "|| within(@org.springframework.web.bind.annotation.RestController *)"
    )
    public void springBeanPointcut() {
    }

    /**
     * Pointcut that matches all Spring beans in the application's main packages.
     */
    @Pointcut(
            "within(com.jellyone..*) " +
                    "|| within(com.jellyone.service..*) " +
                    "|| within(com.jellyone.controller..*)"
    )
    public void applicationPackagePointcut() {
    }

    /**
     * Advice that logs methods throwing exceptions.
     *
     * @param joinPoint join point for advice
     * @param e exception
     */
    @AfterThrowing(pointcut = "applicationPackagePointcut() && springBeanPointcut()", throwing = "e")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable e) {
        log.error(
                "Exception in {}.{}() with cause = {}",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                e.getCause() != null ? e.getCause() : "NULL"
        );
    }

    /**
     * Advice that logs when a method is entered and exited.
     *
     * @param joinPoint join point for advice
     * @return result
     * @throws Throwable throws IllegalArgumentException
     */
    @Around("loggableMethods()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        if (log.isDebugEnabled()) {
            log.debug(
                    "Enter: {}() with argument[] = {}",
                    joinPoint.getSignature().getName(),
                    joinPoint.getArgs()
            );
        }
        try {
            Object result = joinPoint.proceed();
            if (log.isDebugEnabled()) {
                log.debug(
                        "Exit: {}() with result = {}",
                        joinPoint.getSignature().getName(),
                        result
                );
            }
            return result;
        } catch (IllegalArgumentException e) {
            log.error(
                    "Illegal argument: {} in {}.{}()",
                    joinPoint.getArgs(),
                    joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName()
            );
            throw e;
        }
    }
}