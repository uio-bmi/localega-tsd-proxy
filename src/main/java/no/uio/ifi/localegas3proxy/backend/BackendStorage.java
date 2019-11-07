package no.uio.ifi.localegas3proxy.backend;

import java.io.OutputStream;
import java.util.Collection;

public interface BackendStorage {

    Collection<String> list(String username);

    OutputStream getOutputStream(String username, String filename);

}
