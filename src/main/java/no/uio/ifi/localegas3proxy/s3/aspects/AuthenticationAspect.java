package no.uio.ifi.localegas3proxy.s3.aspects;

import lombok.extern.slf4j.Slf4j;
import no.uio.ifi.localegas3proxy.auth.AuthenticationService;
import no.uio.ifi.localegas3proxy.s3.dto.ErrorResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.UUID;

@Slf4j
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
        try {
            authenticationService.authenticate(request);
            return joinPoint.proceed();
        } catch (SecurityException e) {
            String requestedResource = request.getRequestURI();
            String queryString = request.getQueryString();
            if (!StringUtils.isEmpty(queryString)) {
                requestedResource += "?" + queryString;
            }
            UUID requestId = UUID.randomUUID();
            ErrorResponse errorResponse = new ErrorResponse(String.valueOf(HttpStatus.UNAUTHORIZED.value()), e.getMessage(), requestedResource, requestId.toString());
            return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
        }
    }

}
