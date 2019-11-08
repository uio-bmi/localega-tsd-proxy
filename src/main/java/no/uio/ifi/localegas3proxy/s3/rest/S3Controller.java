package no.uio.ifi.localegas3proxy.s3.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController("/")
public class S3Controller {

    @RequestMapping(path = "**", produces = MediaType.APPLICATION_XML_VALUE)
    @ResponseBody
    public ResponseEntity get(HttpServletRequest request) {
        return ResponseEntity.ok("");
    }

}
