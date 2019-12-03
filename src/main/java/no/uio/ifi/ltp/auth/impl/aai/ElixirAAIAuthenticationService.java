package no.uio.ifi.ltp.auth.impl.aai;

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

import java.io.IOException;
import java.security.interfaces.RSAPublicKey;

@Slf4j
@Service
public class ElixirAAIAuthenticationService extends AbstractAuthenticationService {

    @Autowired
    private JWKProvider jwkProvider;

    @Value("${elixir-aai.default-jku}")
    private String defaultJKU;

    @Override
    protected String getSubject(String auth) throws IOException, JwkException {
        DecodedJWT decodedToken = JWT.decode(auth);
        String jku = decodedToken.getHeaderClaim("jku").asString();
        if (StringUtils.isEmpty(jku)) {
            jku = defaultJKU;
        }
        String keyId = decodedToken.getKeyId();
        Jwk jwk = jwkProvider.get(jku, keyId);
        JWTVerifier verifier = JWT.require(Algorithm.RSA256((RSAPublicKey) jwk.getPublicKey(), null)).build();
        DecodedJWT decodedJWT = verifier.verify(auth);
        return decodedJWT.getSubject();
    }

}
