package no.uio.ifi.ltp.rest;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.extern.slf4j.Slf4j;
import no.uio.ifi.ltp.auth.impl.cega.CredentialsProvider;
import no.uio.ifi.ltp.dto.Credentials;
import org.apache.commons.codec.digest.Crypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;

@Slf4j
@RestController
public class AuthController {

    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;

    @Autowired
    private CredentialsProvider credentialsProvider;

    @Autowired
    private String secret;

    @GetMapping("/")
    public ResponseEntity<String> token(OAuth2AuthenticationToken authentication) {
        OAuth2AuthorizedClient oAuth2AuthorizedClient = authorizedClientService.loadAuthorizedClient(authentication.getAuthorizedClientRegistrationId(), authentication.getName());
        log.info("User authenticated: {}", authentication.getPrincipal().getName());
        return ResponseEntity.ok(oAuth2AuthorizedClient.getAccessToken().getTokenValue());
    }

    @GetMapping("/cega")
    public ResponseEntity<String> login(@RequestHeader("Authorization") String authorization) throws MalformedURLException, URISyntaxException {
        String[] usernameAndPassword = new String(Base64.getDecoder().decode(authorization.replace("Basic ", ""))).split(":");
        String username = usernameAndPassword[0];
        Credentials credentials = credentialsProvider.getCredentials(username);
        String hash = credentials.getPasswordHash();
        String password = usernameAndPassword[1];
        if (StringUtils.startsWithIgnoreCase(hash, "$2")
                ? BCrypt.checkpw(password, hash)
                : ObjectUtils.nullSafeEquals(hash, Crypt.crypt(password, hash))) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime inAnHour = now.plusHours(1);
            String token = JWT
                    .create()
                    .withIssuer("LocalEGA")
                    .withIssuedAt(Date.from(now.atZone(ZoneId.systemDefault()).toInstant()))
                    .withExpiresAt(Date.from(inAnHour.atZone(ZoneId.systemDefault()).toInstant()))
                    .withSubject(username)
                    .sign(Algorithm.HMAC512(secret));
            return ResponseEntity.ok(token);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Wrong credentials");
    }


}
