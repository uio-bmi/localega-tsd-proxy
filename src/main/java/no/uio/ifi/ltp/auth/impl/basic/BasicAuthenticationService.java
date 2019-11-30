package no.uio.ifi.ltp.auth.impl.basic;

import com.amdelamar.jhash.Hash;
import lombok.extern.slf4j.Slf4j;
import no.uio.ifi.ltp.auth.impl.AbstractAuthenticationService;
import no.uio.ifi.ltp.dto.Credentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Base64;

import static org.springframework.amqp.support.AmqpHeaders.USER_ID;

@Slf4j
@Service
public class BasicAuthenticationService extends AbstractAuthenticationService {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private CredentialsProvider credentialsProvider;

    @Override
    public boolean authenticate() {
        try {
            String[] usernameAndPassword = new String(Base64.getDecoder().decode(getAuth())).split(":");
            Credentials credentials = credentialsProvider.getCredentials(usernameAndPassword[0]);
            String hash = credentials.getPasswordHash();
            if (Hash.password(usernameAndPassword[1].toCharArray()).verify(hash)) {
                request.setAttribute(USER_ID, usernameAndPassword[0]);
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    protected String getAuth() {
        return request.getHeader("authorization").replace("Basic ", "");
    }

}
