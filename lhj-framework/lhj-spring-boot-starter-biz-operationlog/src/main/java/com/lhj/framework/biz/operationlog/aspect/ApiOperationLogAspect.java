package com.lhj.framework.biz.operationlog.aspect;

import com.lhj.framework.common.util.JsonUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;

@Aspect
public class ApiOperationLogAspect {

    private static final Log log = LogFactory.getLog(ApiOperationLogAspect.class);
//以自定义@ApiOperationLog注解为切点，凡是添加@ApiOperationLog的方法，都会执行环绕中的代码
    @Pointcut("@annotation(com.lhj.framework.biz.operationlog.aspect.ApiOperationLog)")
    public void apiOperationLog() {}

    @Around("apiOperationLog()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        //请求开始时间
        long startTime = System.currentTimeMillis();
        //获取被请求的类和方法
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        //请求入参
        Object[] args = joinPoint.getArgs();
        //入参转JASON字符串
        String argsJsonStr = Arrays.stream(args).map(toJsonStr()).collect(Collectors.joining(", "));
        //功能描述信息
        String description = getApiOperationLogDescription(joinPoint);
        //打印请求相关参数
        log.info(String.format(
                "====== 请求开始: [%s], 入参: %s, 请求类: %s, 请求方法: %s =================================== ",
                description, argsJsonStr, className, methodName));
       //执行切点方法
        Object result = joinPoint.proceed();
        //执行耗时
        long executionTime = System.currentTimeMillis() - startTime;
       //打印出参等相关信息
        log.info(String.format(
                "====== 请求结束: [%s], 耗时: %sms, 出参: %s =================================== ",
                description, executionTime, JsonUtils.toJsonString(result)));

        return result;
    }

//    获取注解的描述信息
//    @param joinPoint
//    @return


    private String getApiOperationLogDescription(ProceedingJoinPoint joinPoint) {
        //1.从ProceedingJoinPoint 获取MethodSignature
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        //2.使用MethodSignature 获取当前被注解的Method
        Method method = signature.getMethod();
        //3.从Metho 中提取 LogExecution 注解
        ApiOperationLog apiOperationLog = method.getAnnotation(ApiOperationLog.class);
        //4.从LogExecution 注解中获取 description
        return apiOperationLog.description();
    }

//    转JSON字符串
//    @return
//
    private Function<Object, String> toJsonStr() {
        return JsonUtils::toJsonString;
    }
}
