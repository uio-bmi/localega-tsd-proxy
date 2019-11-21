package no.uio.ifi.ltp.auth.impl;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import no.uio.ifi.ltp.auth.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.interfaces.RSAPublicKey;

@Slf4j
@Service
public class JWTAuthenticationService implements AuthenticationService {

    public static final String ELIXIR_AAI_TOKEN = "ELIXIR_AAI_TOKEN";
    public static final String ELIXIR_IDENTITY = "ELIXIR_IDENTITY";
    public static final String TSD_TOKEN = "TSD_TOKEN";

    @Autowired
    private JWKProvider jwkProvider;

    @Value("${elixir-aai.default-jku}")
    private String defaultJKU;

    @Override
    public void authenticate(HttpServletRequest request) {
        try {
            String token = validateToken(getToken(request));
            request.setAttribute(ELIXIR_AAI_TOKEN, token);
            DecodedJWT decodedToken = JWT.decode(token);
            String subject = decodedToken.getSubject();
            request.setAttribute(ELIXIR_IDENTITY, subject);
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
        return request.getHeader("authorization").replace("Bearer ", "");
    }

}
