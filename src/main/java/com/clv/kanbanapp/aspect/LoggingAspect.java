package com.clv.kanbanapp.aspect;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;


@Aspect
@Component
@Slf4j
public class LoggingAspect {
    private static final ObjectMapper objectMapper = new ObjectMapper();


    @Pointcut("execution(* com.clv.kanbanapp.controller.*.*(..))")
    public void controllerPointcut() {
    }

    @Pointcut("execution(* com.clv.kanbanapp.service.*.*(..))")
    public void servicePointcut() {
    }

    @Pointcut("execution(* com.clv.kanbanapp.repository.*.*(..))")
    public void repositoryPointcut() {
    }

    @Pointcut("controllerPointcut() || servicePointcut() || repositoryPointcut()")
    public void allPointcut() {
    }


    @Before("allPointcut()")
    public void logBefore(JoinPoint theJoinPoint) {
        Method method = getMethodFromJoinPoint(theJoinPoint);
        if (method != null) {
            log.info("Start method: {}" , theJoinPoint.getSignature());
            Parameter[] parameters = method.getParameters();
            Object[] args = theJoinPoint.getArgs();

            for (int i = 0; i < args.length; i++) {
                String paramName = parameters[i].getName();
                Object argValue = args[i];
                try {
                    String argValueAsString = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(argValue);
                    log.info("Argument name: {}, value: {}", paramName, argValueAsString);
                } catch (JsonProcessingException e) {
                    log.error("Error processing argument value to JSON", e);
                }
            }
        }
    }

    @AfterReturning(pointcut = "allPointcut()", returning = "result")
    public void logAfterReturning(JoinPoint theJoinPoint, Object result) throws JsonProcessingException {
        Method method = getMethodFromJoinPoint(theJoinPoint);

        if (method != null) {
            log.info("End method: {} returned: {}", theJoinPoint.getSignature(), result);
        }
    }

    private Method getMethodFromJoinPoint(JoinPoint joinPoint) {
        Method method = null;
        try {
            String methodName = joinPoint.getSignature().getName();
            Class<?>[] parameterTypes = ((org.aspectj.lang.reflect.MethodSignature) joinPoint.getSignature()).getMethod().getParameterTypes();
            method = joinPoint.getTarget().getClass().getMethod(methodName, parameterTypes);
        } catch (NoSuchMethodException e) {
            log.error("Method not found", e);
        }
        return method;
    }
}
