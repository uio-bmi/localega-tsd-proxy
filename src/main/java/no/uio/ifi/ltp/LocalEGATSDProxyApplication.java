package no.uio.ifi.ltp;

import no.uio.ifi.tc.TSDFileAPIClient;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.*;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;

@EnableCaching
@SpringBootApplication
public class LocalEGATSDProxyApplication extends WebSecurityConfigurerAdapter {

    @Value("${tsd.host}")
    private String tsdHost;

    @Value("${tsd.access-key}")
    private String tsdAccessKey;

    @Value("${tsd.root-ca}")
    private String tsdRootCA;

    @Value("${spring.security.oauth2.client.registration.elixir-aai.redirect-uri}")
    private String redirectURI;

    public static void main(String[] args) {
        SpringApplication.run(LocalEGATSDProxyApplication.class, args);
    }

    @Bean
    public TSDFileAPIClient tsdFileAPIClient() throws GeneralSecurityException, IOException {
        X509TrustManager trustManager = trustManagerForCertificates(Files.newInputStream(Path.of(tsdRootCA)));
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, new TrustManager[]{trustManager}, null);
        CloseableHttpClient httpClient = HttpClients.custom().setSSLContext(sslContext).build();
        return new TSDFileAPIClient.Builder()
                .httpClient(httpClient)
                .host(tsdHost)
                .accessKey(tsdAccessKey)
                .build();
    }

    @Bean
    public String secret() {
        return UUID.randomUUID().toString();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        String baseURI = redirectURI.substring(redirectURI.lastIndexOf("/"));
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers("/").authenticated()
                .antMatchers("/**").permitAll()
                .and()
                .oauth2Login()
                .defaultSuccessUrl("/")
                .redirectionEndpoint().baseUri(baseURI);
    }

    private X509TrustManager trustManagerForCertificates(InputStream in) throws GeneralSecurityException {
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        Collection<? extends Certificate> certificates = certificateFactory.generateCertificates(in);
        if (certificates.isEmpty()) {
            throw new IllegalArgumentException("Expected non-empty set of trusted certificates");
        }

        // put the certificates into a key store
        char[] password = UUID.randomUUID().toString().toCharArray(); // any password will do
        KeyStore keyStore = newEmptyKeyStore(password);
        for (Certificate certificate : certificates) {
            keyStore.setCertificateEntry(UUID.randomUUID().toString(), certificate);
        }

        // use it to build an X509 trust manager
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, password);
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(keyStore);
        TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
        if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
            throw new IllegalStateException("Unexpected default trust managers: " + Arrays.toString(trustManagers));
        }
        return (X509TrustManager) trustManagers[0];
    }

    private KeyStore newEmptyKeyStore(char[] password) throws GeneralSecurityException {
        try {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, password);
            return keyStore;
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

}
