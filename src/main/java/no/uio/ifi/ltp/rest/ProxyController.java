package no.uio.ifi.ltp.rest;

import kong.unirest.json.JSONObject;
import lombok.extern.slf4j.Slf4j;
import no.uio.ifi.tc.TSDFileAPIClient;
import no.uio.ifi.tc.TokenType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Slf4j
@RestController("/")
public class ProxyController {

    @Autowired
    private TSDFileAPIClient tsdFileAPIClient;

    @PutMapping("/upload/{fileName}")
    @ResponseBody
    public ResponseEntity upload(InputStream inputStream, @PathVariable("fileName") String fileName) throws IOException {
        String token = tsdFileAPIClient.getToken(TokenType.IMPORT);
        String result = tsdFileAPIClient.upload(token, inputStream, fileName);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/resumables")
    @ResponseBody
    public ResponseEntity resumables() {
        String token = tsdFileAPIClient.getToken(TokenType.IMPORT);
        List<JSONObject> result = tsdFileAPIClient.getResumableUploads(token);
        return ResponseEntity.ok(result);
    }

}
