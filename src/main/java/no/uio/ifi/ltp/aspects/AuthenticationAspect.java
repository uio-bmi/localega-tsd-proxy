package no.uio.ifi.ltp.aspects;

import lombok.extern.slf4j.Slf4j;
import no.uio.ifi.ltp.auth.AuthenticationService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

@Slf4j
@Aspect
@Component
public class AuthenticationAspect {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private AuthenticationService authenticationService;

    @Around("execution(public * no.uio.ifi.ltp.rest.ProxyController.*(..))")
    public Object authenticate(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            authenticationService.authenticate(request);
            return joinPoint.proceed();
        } catch (SecurityException e) {
            UUID requestId = UUID.randomUUID();
            log.error("Request ID: {}, Error: {}", requestId, e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

}
