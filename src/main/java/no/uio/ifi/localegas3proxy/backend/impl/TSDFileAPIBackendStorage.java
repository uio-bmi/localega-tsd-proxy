package no.uio.ifi.localegas3proxy.backend.impl;

import no.uio.ifi.localegas3proxy.backend.BackendStorage;
import no.uio.ifi.localegas3proxy.backend.model.BackendFile;
import org.springframework.stereotype.Component;

import java.io.OutputStream;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

@Component
public class TSDFileAPIBackendStorage implements BackendStorage {

    @Override
    public Collection<BackendFile> list(String username) {
        Instant instant = new Date().toInstant();
        return Arrays.asList(
                new BackendFile("top.jpg", instant, 42),
                new BackendFile("subfolder1/1.jpg", instant, 42),
                new BackendFile("subfolder1/2.jpg", instant, 42),
                new BackendFile("subfolder1/3.jpg", instant, 42),
                new BackendFile("subfolder2/4.jpg", instant, 42),
                new BackendFile("subfolder2/5.jpg", instant, 42),
                new BackendFile("subfolder2/6.jpg", instant, 42),
                new BackendFile("subfolder2/subfolder3/image.jpg", instant, 42),
                new BackendFile("subfolder4/subfolder5/photo.jpg", instant, 42)
        );
    }

    @Override
    public OutputStream getOutputStream(String username, String filename) {
        return null;
    }

}
