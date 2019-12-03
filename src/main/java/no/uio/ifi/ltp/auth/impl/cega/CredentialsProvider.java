package no.uio.ifi.ltp.auth.impl.cega;

import no.uio.ifi.ltp.dto.Credentials;
import no.uio.ifi.ltp.dto.ResponseHolder;
import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Base64;
import java.util.Objects;

/**
 * Component that queries CEGA for user credentials.
 */
@Component
public class CredentialsProvider {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${cega.auth-url}")
    private String cegaAuthURL;

    @Value("${cega.username}")
    private String cegaUsername;

    @Value("${cega.password}")
    private String cegaPassword;

    @Cacheable("cega-credentials")
    public Credentials getCredentials(String username) throws MalformedURLException, URISyntaxException {
        URL url = new URL(String.format(cegaAuthURL, username));
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString((cegaUsername + ":" + cegaPassword).getBytes()));
        ResponseEntity<ResponseHolder> response = restTemplate.exchange(url.toURI(), HttpMethod.GET, new HttpEntity<>(headers), ResponseHolder.class);
        return Objects.requireNonNull(response.getBody()).getResultsHolder().getCredentials().iterator().next();
    }

}
