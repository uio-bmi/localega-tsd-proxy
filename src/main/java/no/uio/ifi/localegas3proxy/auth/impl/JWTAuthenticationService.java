package no.uio.ifi.localegas3proxy.auth.impl;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import no.uio.ifi.localegas3proxy.auth.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import uk.co.lucasweb.aws.v4.signer.HttpRequest;
import uk.co.lucasweb.aws.v4.signer.Signer;
import uk.co.lucasweb.aws.v4.signer.credentials.AwsCredentials;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.interfaces.RSAPublicKey;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class JWTAuthenticationService implements AuthenticationService {

    private static final Pattern AWS_AUTH4_PATTERN = Pattern.compile("AWS4-HMAC-SHA256 Credential=([^/]+)/([^/]+)/([^/]+)/s3/aws4_request, SignedHeaders=([^,]+), Signature=(.+)");

    @Autowired
    private JWKProvider jwkProvider;

    @Override
    public boolean authenticate(HttpServletRequest request) {
        Optional<String> tokenOptional = getToken(request);
        if (tokenOptional.isEmpty()) {
            return false;
        }
        String token = tokenOptional.get();
        return validateToken(token);
    }

    private boolean validateToken(String token) {
        try {
            DecodedJWT decodedToken = JWT.decode(token);
            String jku = decodedToken.getHeaderClaim("jku").asString();
            String keyId = decodedToken.getKeyId();
            Jwk jwk = jwkProvider.get(jku, keyId);
            JWTVerifier verifier = JWT.require(Algorithm.RSA256((RSAPublicKey) jwk.getPublicKey(), null)).build();
            verifier.verify(token);
            return true;
        } catch (JWTVerificationException | JwkException | IOException e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    private Optional<String> getToken(HttpServletRequest request) {
        String authorization = request.getHeader("authorization");
        Matcher matcher = AWS_AUTH4_PATTERN.matcher(authorization);
        if (!matcher.matches()) {
            log.error("Authorization header missing or doesn't match the AWS Signature V4 pattern");
            return Optional.empty();
        }
        String requestURL = request.getRequestURL().toString();
        String queryString = request.getQueryString();
        if (!StringUtils.isEmpty(queryString)) {
            requestURL += "?" + queryString;
        }
        HttpRequest httpRequest;
        try {
            httpRequest = new HttpRequest(request.getMethod(), new URI(requestURL));
        } catch (URISyntaxException e) {
            log.error(e.getMessage(), e);
            return Optional.empty();
        }

        String token = matcher.group(1);
        Signer.Builder builder = Signer.builder().awsCredentials(new AwsCredentials(token, token));
        String[] headers = matcher.group(4).split(";");
        for (String header : headers) {
            builder = builder.header(header, request.getHeader(header));
        }
        String digest = request.getHeader("x-amz-content-sha256");
        String signature = builder.buildS3(httpRequest, digest).getSignature();
        if (signature.equals(authorization)) {
            log.error("Signatures don't match");
            return Optional.of(token);
        } else {
            return Optional.empty();
        }
    }

}
