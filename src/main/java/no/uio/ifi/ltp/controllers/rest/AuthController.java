package no.uio.ifi.ltp.controllers.rest;

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

/**
 * REST controller that handles OIDC authentication.
 */
@Slf4j
@RestController
public class AuthController {

    private final OAuth2AuthorizedClientService authorizedClientService;

    @Autowired
    public AuthController(OAuth2AuthorizedClientService authorizedClientService) {
        this.authorizedClientService = authorizedClientService;
    }

    /**
     * Returns access token.
     * @param authentication <code>OAuth2AuthenticationToken</code>.
     * @return Access token.
     */
    @GetMapping("/token")
    public ResponseEntity<String> index(OAuth2AuthenticationToken authentication) {
        OAuth2AuthorizedClient oAuth2AuthorizedClient = authorizedClientService.loadAuthorizedClient(authentication.getAuthorizedClientRegistrationId(), authentication.getName());
        log.info("User authenticated: {}", authentication.getPrincipal().getName());
        return ResponseEntity.ok(oAuth2AuthorizedClient.getAccessToken().getTokenValue());
    }

    /**
     * Returns OIDC principal.
     * @param principal <code>OidcUser</code>.
     * @return OIDC principal.
     */
    @GetMapping("/user")
    public ResponseEntity<OidcUser> token(@AuthenticationPrincipal OidcUser principal) {
        log.info("User authenticated: {}", principal.getName());
        return ResponseEntity.ok(principal);
    }

}
