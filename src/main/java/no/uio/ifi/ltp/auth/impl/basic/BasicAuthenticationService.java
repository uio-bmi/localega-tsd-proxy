package no.uio.ifi.ltp.auth.impl.basic;

import lombok.extern.slf4j.Slf4j;
import no.uio.ifi.ltp.auth.impl.AbstractAuthenticationService;
import no.uio.ifi.ltp.dto.Credentials;
import org.apache.commons.codec.digest.Crypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

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
            String username = usernameAndPassword[0];
            Credentials credentials = credentialsProvider.getCredentials(username);
            String hash = credentials.getPasswordHash();
            String password = usernameAndPassword[1];
            if (StringUtils.startsWithIgnoreCase(hash, "$2")
                    ? BCrypt.checkpw(password, hash)
                    : ObjectUtils.nullSafeEquals(hash, Crypt.crypt(password, hash))) {
                request.setAttribute(USER_ID, username);
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
