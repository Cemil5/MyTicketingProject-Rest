package com.cydeo.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Aspect
@Configuration
public class PerformanceAspect {

    Logger logger = LoggerFactory.getLogger(PerformanceAspect.class);

    @Pointcut("@annotation(com.cydeo.annotation.ExecutionTime)")
    private void anyExecutionTimeOperation(){}

    @Around("anyExecutionTimeOperation()")
    public void anyExecutionTimeOperationAdvice(ProceedingJoinPoint proceedingJoinPoint){
        long beforeTime = System.currentTimeMillis();
        Object result = null;

        try {
            result = proceedingJoinPoint.proceed();
        } catch (Throwable throwable){
            throwable.printStackTrace();
        }
        long afterTime = System.currentTimeMillis();

        logger.info("Time taken to execute :{} ms (Method :{} - Parameters :{})", (afterTime-beforeTime),
                proceedingJoinPoint.getSignature().toShortString(), proceedingJoinPoint.getArgs());
    }



}
