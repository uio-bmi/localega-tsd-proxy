package no.uio.ifi.ltp.aspects;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

import static no.uio.ifi.ltp.auth.impl.JWTAuthenticationService.ELIXIR_IDENTITY;

@Slf4j
@Aspect
@Order(2)
@Component
public class FileNameAspect {

    @Autowired
    private HttpServletRequest request;

    @Around("execution(public * no.uio.ifi.ltp.rest.ProxyController.*(..))")
    public Object replaceFileName(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] arguments = joinPoint.getArgs();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] parameterNames = signature.getParameterNames();
        Class[] parameterTypes = signature.getParameterTypes();
        for (int i = 0; i < arguments.length; i++) {
            if (parameterTypes[i].equals(String.class) && parameterNames[i].equalsIgnoreCase("fileName")) {
                arguments[i] = getFullFileName(arguments[i].toString());
            }
        }
        return joinPoint.proceed(arguments);
    }

    private String getFullFileName(String fileName) {
        String elixirIdentity = request.getAttribute(ELIXIR_IDENTITY).toString();
        int atIndex = elixirIdentity.lastIndexOf("@");
        String prefix = elixirIdentity.substring(0, atIndex);
        return prefix + "-" + fileName;
    }

}
