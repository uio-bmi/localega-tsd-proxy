package no.uio.ifi.ltp.auth.impl.cega;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.extern.slf4j.Slf4j;
import no.uio.ifi.ltp.auth.impl.AbstractAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CEGAAuthenticationService extends AbstractAuthenticationService {

    @Autowired
    private String secret;

    @Override
    protected String getSubject(String auth) {
        return JWT.require(Algorithm.HMAC512(secret)).build().verify(auth).getSubject();
    }

}
