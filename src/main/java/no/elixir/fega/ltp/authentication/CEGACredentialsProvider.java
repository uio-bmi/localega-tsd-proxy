package no.elixir.fega.ltp.authentication;

import no.elixir.fega.ltp.dto.Credentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Base64;
import java.util.Objects;

/**
 * Component that queries CEGA for user credentials.
 */
@Component
public class CEGACredentialsProvider {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${cega.auth-url}")
    private String cegaAuthURL;

    @Value("${cega.username}")
    private String cegaUsername;

    @Value("${cega.password}")
    private String cegaPassword;

    /**
     * Gets CEGA credentials from CEGA auth endpoint, the method is cached.
     *
     * @param username CEGA username.
     * @return <code>Credentials</code> POJO.
     * @throws MalformedURLException In case CEGA auth endpoint URL is malformed.
     * @throws URISyntaxException    In case CEGA auth endpoint URL is malformed.
     */
    @Cacheable("cega-credentials")
    public Credentials getCredentials(String username) throws MalformedURLException, URISyntaxException {
        URL url = new URI(String.format(cegaAuthURL + "%s?idType=username", username)).toURL();
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString((cegaUsername + ":" + cegaPassword).getBytes()));
        ResponseEntity<Credentials> response = restTemplate.exchange(url.toURI(), HttpMethod.GET,
                new HttpEntity<>(headers),
                Credentials.class);
        return Objects.requireNonNull(response.getBody());
    }

}
