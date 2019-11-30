package no.uio.ifi.ltp.aspects;

import lombok.extern.slf4j.Slf4j;
import no.uio.ifi.ltp.auth.AuthenticationService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Slf4j
@Aspect
@Order(1)
@Component
public class AuthenticationAspect {

    @Autowired
    private Collection<AuthenticationService> authenticationServices;

    @Around("execution(public * no.uio.ifi.ltp.rest.ProxyController.*(..))")
    public Object authenticate(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            if (authenticationServices.stream().anyMatch(AuthenticationService::authenticate)) {
                return joinPoint.proceed();
            }
            throw new SecurityException();
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

}
