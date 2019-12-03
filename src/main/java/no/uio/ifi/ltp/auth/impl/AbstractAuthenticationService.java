package no.uio.ifi.ltp.auth.impl;

import com.auth0.jwk.JwkException;
import lombok.extern.slf4j.Slf4j;
import no.uio.ifi.ltp.auth.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

import java.io.IOException;

import static org.springframework.amqp.support.AmqpHeaders.USER_ID;

@Slf4j
@Service
public abstract class AbstractAuthenticationService implements AuthenticationService {

    @Autowired
    protected HttpServletRequest request;

    @Override
    public boolean authenticate() {
        try {
            request.setAttribute(USER_ID, getSubject(getAuth()));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    protected String getAuth() {
        return request.getHeader("authorization").replace("Bearer ", "");
    }

    protected abstract String getSubject(String auth) throws IOException, JwkException;

}
