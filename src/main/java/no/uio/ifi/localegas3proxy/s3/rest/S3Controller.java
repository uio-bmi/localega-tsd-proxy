package no.uio.ifi.localegas3proxy.s3.rest;

import lombok.extern.slf4j.Slf4j;
import no.uio.ifi.localegas3proxy.auth.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController("/")
public class S3Controller {

    @Autowired
    private AuthenticationService authenticationService;

    @RequestMapping("**")
    public void get(HttpServletRequest request) {
        if (!authenticationService.authenticate(request)) {
            throw new RuntimeException();
        }
    }

}
