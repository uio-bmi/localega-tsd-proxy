package no.uio.ifi.ltp.auth;

public interface AuthenticationService {

    boolean authenticate() throws SecurityException;

}
