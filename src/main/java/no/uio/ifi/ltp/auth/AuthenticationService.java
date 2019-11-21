package no.uio.ifi.ltp.auth;

import javax.servlet.http.HttpServletRequest;

public interface AuthenticationService {

    void authenticate(HttpServletRequest request) throws SecurityException;

}
