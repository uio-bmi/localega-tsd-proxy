package no.uio.ifi.ltp.aspects;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import no.uio.ifi.clearinghouse.Clearinghouse;
import no.uio.ifi.clearinghouse.model.Visa;
import no.uio.ifi.clearinghouse.model.VisaType;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Aspect
@Order(1)
@Component
public class AAIAspect {

    @Value("${elixir.openid-configuration-url}")
    private String openIDConfigurationURL;

    @Autowired
    protected HttpServletRequest request;

    @Around("execution(public * no.uio.ifi.ltp.rest.ProxyController.*(..))")
    public Object authenticate(ProceedingJoinPoint joinPoint) throws Throwable {
        Optional<String> optionalToken = getJWTToken();
        if (optionalToken.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String jwtToken = optionalToken.get().replace("Bearer ", "");
        try {
            DecodedJWT decodedJWT = JWT.decode(jwtToken);
            boolean isVisa = decodedJWT.getClaims().containsKey("ga4gh_visa_v1");
            Collection<Visa> visas = new ArrayList<>();
            if (isVisa) {
                getVisa(jwtToken).ifPresent(visas::add);
            } else {
                visas.addAll(getVisas(jwtToken));
            }
            List<Visa> controlledAccessGrantsVisas = visas
                    .stream()
                    .filter(v -> v.getType().equalsIgnoreCase(VisaType.ControlledAccessGrants.name()))
                    .collect(Collectors.toList());
            log.info("Authentication and authorization attempt. User {} provided following valid GA4GH Visas: {}", decodedJWT.getSubject(), controlledAccessGrantsVisas);
            Set<String> datasets = controlledAccessGrantsVisas
                    .stream()
                    .map(Visa::getValue)
                    .map(d -> StringUtils.stripEnd(d, "/"))
                    .map(d -> StringUtils.substringAfterLast(d, "/"))
                    .collect(Collectors.toSet());
            log.info("User has access to the following resources: {}", datasets);
            return joinPoint.proceed();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    protected Collection<Visa> getVisas(String accessToken) {
        Collection<String> visaTokens = Clearinghouse.INSTANCE.getVisaTokens(accessToken, openIDConfigurationURL);
        return visaTokens
                .stream()
                .map(this::getVisa)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    protected Optional<Visa> getVisa(String visaToken) {
        return Clearinghouse.INSTANCE.getVisa(visaToken);
    }

    protected Optional<String> getJWTToken() {
        return Optional.ofNullable(request.getHeader(HttpHeaders.AUTHORIZATION));
    }

}
