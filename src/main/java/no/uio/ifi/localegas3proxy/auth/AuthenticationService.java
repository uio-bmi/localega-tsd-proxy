package no.uio.ifi.localegas3proxy.auth;

import javax.servlet.http.HttpServletRequest;

public interface AuthenticationService {

    void authenticate(HttpServletRequest request) throws SecurityException;

}
