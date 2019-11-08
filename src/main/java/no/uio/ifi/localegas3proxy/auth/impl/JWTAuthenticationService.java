package no.uio.ifi.localegas3proxy.auth.impl;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class JWTAuthenticationService implements AuthenticationService {

    private static final Pattern AWS_AUTH4_PATTERN = Pattern.compile("AWS4-HMAC-SHA256 Credential=([^/]+)/([^/]+)/([^/]+)/s3/aws4_request, SignedHeaders=([^,]+), Signature=(.+)");

    @Autowired
    private JWKProvider jwkProvider;

    @Override
    public String authenticate(HttpServletRequest request) {
        try {
            return validateToken(getToken(request));
        } catch (Exception e) {
            throw new SecurityException(e.getMessage());
        }
    }

    private String validateToken(String token) throws IOException, JwkException {
        DecodedJWT decodedToken = JWT.decode(token);
        String jku = decodedToken.getHeaderClaim("jku").asString();
        String keyId = decodedToken.getKeyId();
        Jwk jwk = jwkProvider.get(jku, keyId);
        JWTVerifier verifier = JWT.require(Algorithm.RSA256((RSAPublicKey) jwk.getPublicKey(), null)).build();
        verifier.verify(token);
        return token;
    }

    private String getToken(HttpServletRequest request) throws URISyntaxException {
        String authorization = request.getHeader("authorization");
        if (StringUtils.isEmpty(authorization)) {
            throw new SecurityException("Authorization header missing");
        }
        Matcher matcher = AWS_AUTH4_PATTERN.matcher(authorization);
        if (!matcher.matches()) {
            throw new SecurityException("Authorization header doesn't match the AWS Signature V4 pattern");
        }
        String requestURL = request.getRequestURL().toString();
        String queryString = request.getQueryString();
        if (!StringUtils.isEmpty(queryString)) {
            requestURL += "?" + queryString;
        }
        HttpRequest httpRequest = new HttpRequest(request.getMethod(), new URI(requestURL));

        String token = matcher.group(1);
        Signer.Builder builder = Signer.builder().awsCredentials(new AwsCredentials(token, token));
        String[] headers = matcher.group(4).split(";");
        for (String header : headers) {
            builder = builder.header(header, request.getHeader(header));
        }
        String digest = request.getHeader("x-amz-content-sha256");
        String signature = builder.buildS3(httpRequest, digest).getSignature();
        if (signature.equals(authorization)) {
            return token;
        } else {
            throw new SecurityException("Signatures don't match");
        }
    }

}
