package no.uio.ifi.ltp.auth.impl.basic;

import kong.unirest.Unirest;
import no.uio.ifi.ltp.dto.Credentials;
import no.uio.ifi.ltp.dto.ResponseHolder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

/**
 * Component that queries CEGA for user credentials.
 */
@Component
public class CredentialsProvider {

    @Value("${cega.auth-url}")
    private String cegaAuthURL;

    @Value("${cega.username}")
    private String cegaUsername;

    @Value("${cega.password}")
    private String cegaPassword;

    @Cacheable("cega-credentials")
    public Credentials getCredentials(String username) {
        return Unirest
                .get(String.format(cegaAuthURL, username))
                .basicAuth(cegaUsername, cegaPassword)
                .asObject(ResponseHolder.class)
                .getBody()
                .getResultsHolder()
                .getCredentials().iterator().next();
    }

}
