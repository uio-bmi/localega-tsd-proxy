package no.uio.ifi.ltp.auth.impl;

import lombok.extern.slf4j.Slf4j;
import no.uio.ifi.ltp.auth.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Service
public abstract class AbstractAuthenticationService implements AuthenticationService {

    @Autowired
    protected HttpServletRequest request;

    protected abstract String getAuth();

}
