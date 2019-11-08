package no.uio.ifi.localegas3proxy.backend.impl;

import no.uio.ifi.localegas3proxy.backend.BackendStorage;
import org.springframework.stereotype.Component;

import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

@Component
public class TSDFileAPIBackendStorage implements BackendStorage {

    @Override
    public Collection<String> listAllMyBuckets(String username) {
        return Collections.singleton("LocalEGA");
    }

    @Override
    public Collection<String> listBucket(String username, String path) {
        return Arrays.asList("folder1", "folder2");
    }

    @Override
    public OutputStream getOutputStream(String username, String filename) {
        return null;
    }

}
