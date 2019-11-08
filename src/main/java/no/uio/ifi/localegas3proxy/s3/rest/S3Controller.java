package no.uio.ifi.localegas3proxy.s3.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController("/")
public class S3Controller {

    @RequestMapping("**")
    public void get(HttpServletRequest request) {

    }

}
