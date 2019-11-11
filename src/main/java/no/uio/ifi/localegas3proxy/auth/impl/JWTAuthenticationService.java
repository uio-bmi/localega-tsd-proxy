package no.uio.ifi.localegas3proxy.auth.impl;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import no.uio.ifi.localegas3proxy.auth.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.interfaces.RSAPublicKey;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class JWTAuthenticationService implements AuthenticationService {

    public static final String JWT_TOKEN = "JWT_TOKEN";
    public static final String JWT_SUBJECT = "JWT_SUBJECT";

    private static final Pattern AWS_AUTH_PATTERN = Pattern.compile("AWS ([^:]+):(.+)");
    private static final Pattern AWS_AUTH4_PATTERN = Pattern.compile("AWS4-HMAC-SHA256 Credential=([^/]+)/([^/]+)/([^/]+)/s3/aws4_request, SignedHeaders=([^,]+), Signature=(.+)");

    @Autowired
    private JWKProvider jwkProvider;

    @Value("${default_jku}")
    private String defaultJKU;

    @Override
    public void authenticate(HttpServletRequest request) {
        try {
            String token = validateToken(getToken(request));
            request.setAttribute(JWT_TOKEN, token);
            DecodedJWT decodedToken = JWT.decode(token);
            String subject = decodedToken.getSubject();
            request.setAttribute(JWT_SUBJECT, subject);
        } catch (Exception e) {
            throw new SecurityException(e.getMessage());
        }
    }

    private String validateToken(String token) throws IOException, JwkException {
        DecodedJWT decodedToken = JWT.decode(token);
        String jku = decodedToken.getHeaderClaim("jku").asString();
        if (StringUtils.isEmpty(jku)) {
            jku = defaultJKU;
        }
        String keyId = decodedToken.getKeyId();
        Jwk jwk = jwkProvider.get(jku, keyId);
        JWTVerifier verifier = JWT.require(Algorithm.RSA256((RSAPublicKey) jwk.getPublicKey(), null)).build();
        verifier.verify(token);
        return token;
    }

    private String getToken(HttpServletRequest request) {
        String authorization = request.getHeader("authorization");
        if (StringUtils.isEmpty(authorization)) {
            throw new SecurityException("Authorization header missing");
        }
        Matcher matcher = AWS_AUTH4_PATTERN.matcher(authorization);
        if (matcher.matches()) {
            return matcher.group(1);
        }
        matcher = AWS_AUTH_PATTERN.matcher(authorization);
        if (matcher.matches()) {
            return matcher.group(1);
        }
        throw new SecurityException("Authorization header doesn't match the AWS Signature V4 pattern");
    }

}
