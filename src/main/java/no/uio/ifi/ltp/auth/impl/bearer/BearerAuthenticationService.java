package no.uio.ifi.ltp.auth.impl.bearer;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import no.uio.ifi.ltp.auth.impl.AbstractAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.interfaces.RSAPublicKey;

import static org.springframework.amqp.support.AmqpHeaders.USER_ID;

@Slf4j
@Service
public class BearerAuthenticationService extends AbstractAuthenticationService {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private JWKProvider jwkProvider;

    @Value("${elixir-aai.default-jku}")
    private String defaultJKU;

    @Override
    public boolean authenticate() {
        try {
            String token = validateToken(getAuth());
            DecodedJWT decodedToken = JWT.decode(token);
            String subject = decodedToken.getSubject();
            request.setAttribute(USER_ID, subject);
            return true;
        } catch (Exception e) {
            return false;
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

    @Override
    protected String getAuth() {
        return request.getHeader("authorization").replace("Bearer ", "");
    }

}
