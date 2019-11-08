package no.uio.ifi.localegas3proxy.backend;

import java.io.OutputStream;
import java.util.Collection;

public interface BackendStorage {

    Collection<String> listAllMyBuckets(String username);

    Collection<String> listBucket(String username, String path);

    OutputStream getOutputStream(String username, String filename);

}
