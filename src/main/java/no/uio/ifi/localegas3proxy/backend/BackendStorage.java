package no.uio.ifi.localegas3proxy.backend;

import no.uio.ifi.localegas3proxy.backend.model.BackendFile;

import java.io.OutputStream;
import java.util.Collection;

public interface BackendStorage {

    Collection<BackendFile> list(String username);

    OutputStream getOutputStream(String username, String filename);

}
