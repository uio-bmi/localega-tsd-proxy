package no.uio.ifi.ltp.rest;

import lombok.extern.slf4j.Slf4j;
import no.uio.ifi.tc.TSDFileAPIClient;
import no.uio.ifi.tc.TokenType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Slf4j
@RestController("/")
public class ProxyController {

    @Autowired
    private TSDFileAPIClient tsdFileAPIClient;

    @PutMapping("/upload/${fileName}")
    @ResponseBody
    public ResponseEntity upload(HttpServletRequest request,
                                 @PathVariable("fileName") String fileName) throws IOException {
        String token = tsdFileAPIClient.getToken(TokenType.IMPORT);
        String result = tsdFileAPIClient.upload(token, request.getInputStream(), fileName);
        return ResponseEntity.ok(result);
    }

}
