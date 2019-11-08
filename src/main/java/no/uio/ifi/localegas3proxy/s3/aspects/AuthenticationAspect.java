package no.uio.ifi.localegas3proxy.s3.aspects;

import no.uio.ifi.localegas3proxy.auth.AuthenticationService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.Charset;
import java.util.Arrays;

@Aspect
@Component
public class AuthenticationAspect {

    @Autowired
    private AuthenticationService authenticationService;

    @Around("@annotation(org.springframework.web.bind.annotation.RequestMapping) && args(javax.servlet.http.HttpServletRequest)")
    public Object authenticate(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpClientErrorException unauthorizedException = HttpClientErrorException.Unauthorized.create(HttpStatus.UNAUTHORIZED, "Unauthorized", HttpHeaders.EMPTY, null, Charset.defaultCharset());
        HttpServletRequest request = Arrays.stream(joinPoint.getArgs())
                .filter(a -> a instanceof HttpServletRequest)
                .findAny()
                .map(r -> (HttpServletRequest) r)
                .orElseThrow(() -> unauthorizedException);
        if (authenticationService.authenticate(request)) {
            return joinPoint.proceed();
        } else {
            throw unauthorizedException;
        }
    }

}
