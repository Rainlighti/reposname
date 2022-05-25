package cn.voidnet.scoresystem.share.auth;

import cn.voidnet.scoresystem.share.auth.exception.AuthenticationFailException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;

@Aspect
@Component
public class AccessControlAspect {

    @Resource
    AuthService authService;

    private final static Logger logger = LoggerFactory.getLogger(AccessControlAspect.class);

    @Pointcut(value = "@annotation(cn.voidnet.scoresystem.share.auth.AccessControl)")
    public void accessControlPointCut() {
    }

    @Around("accessControlPointCut()")
    public Object before(ProceedingJoinPoint joinPoint) throws Throwable {
        //依据性能来判断是否需要优化 避免不必要的SQL Query
        var currUser =
                Optional.of(
                        ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder
                                .getRequestAttributes())))
                        .map(ServletRequestAttributes::getRequest)
                        .map(req -> req.getHeader("token"))
                        .map(authService::findUserIfLogin)
                        .orElseThrow(AuthenticationFailException::new);
        Optional
                .of(((MethodSignature) joinPoint.getSignature()))
                .map(MethodSignature::getMethod)
                .map(it -> it.getAnnotation(AccessControl.class))
                .map(AccessControl::value)
                .filter(it -> it.length != 0)
                .ifPresent(
                        allowTypes ->
                                Arrays.stream(allowTypes)
                                        .filter(currUser.getType()::equals)
                                        .findAny()
                                        .orElseThrow(AuthenticationFailException::new)
                );
        var parameters = ((MethodSignature) joinPoint.getSignature())
                .getMethod()
                .getParameters();
        return joinPoint.proceed(
                IntStream.range(0, parameters.length)
                        .mapToObj(i ->
                                (parameters[i].getAnnotationsByType(CurrUser.class).length >= 1)
                                        ? currUser
                                        : joinPoint.getArgs()[i]
                        )
                        .toArray()
        );
    }
}
