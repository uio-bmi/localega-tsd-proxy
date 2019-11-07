package no.uio.ifi.localegas3proxy.auth.impl;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkException;
import com.auth0.jwk.SigningKeyNotFoundException;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class JWKProvider {

    @Autowired
    private Gson gson;

    @SuppressWarnings("unchecked")
    public List<Jwk> getAll(String url) throws IOException {
        try (InputStreamReader reader = new InputStreamReader(new URL(url).openStream())) {
            Map content = gson.fromJson(reader, Map.class);
            List<Map<String, Object>> keys = (List<Map<String, Object>>) content.get("keys");
            return keys.stream().map(Jwk::fromValues).collect(Collectors.toList());
        }
    }

    @Cacheable("jwk-keys")
    public Jwk get(String url, String keyId) throws JwkException, IOException {
        return getAll(url)
                .stream()
                .filter(k -> k.getId().equals(keyId))
                .findAny()
                .orElseThrow(() -> new SigningKeyNotFoundException("No key found in " + url + " with kid " + keyId, null));
    }

}
