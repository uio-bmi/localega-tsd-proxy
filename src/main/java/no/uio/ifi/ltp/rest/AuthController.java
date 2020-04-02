package no.uio.ifi.ltp.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class AuthController {

    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;

    @GetMapping("/")
    public ResponseEntity<String> index(OAuth2AuthenticationToken authentication) {
        OAuth2AuthorizedClient oAuth2AuthorizedClient = authorizedClientService.loadAuthorizedClient(authentication.getAuthorizedClientRegistrationId(), authentication.getName());
        log.info("User authenticated: {}", authentication.getPrincipal().getName());
        return ResponseEntity.ok(oAuth2AuthorizedClient.getAccessToken().getTokenValue());
    }

    @GetMapping("/token")
    public ResponseEntity<OidcUser> token(@AuthenticationPrincipal OidcUser principal) {
        log.info("User authenticated: {}", principal.getName());
        return ResponseEntity.ok(principal);
    }

}
