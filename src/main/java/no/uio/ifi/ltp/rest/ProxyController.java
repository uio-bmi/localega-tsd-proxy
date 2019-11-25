package no.uio.ifi.ltp.rest;

import lombok.extern.slf4j.Slf4j;
import no.uio.ifi.tc.TSDFileAPIClient;
import no.uio.ifi.tc.model.TokenType;
import no.uio.ifi.tc.model.pojo.Chunk;
import no.uio.ifi.tc.model.pojo.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@RestController("/")
public class ProxyController {

    @Autowired
    private TSDFileAPIClient tsdFileAPIClient;

    @PutMapping("/upload/{fileName}")
    @ResponseBody
    public ResponseEntity upload(InputStream inputStream,
                                 @PathVariable("fileName") String fileName) throws IOException {
        Token token = tsdFileAPIClient.getToken(TokenType.IMPORT);
        return ResponseEntity.ok(tsdFileAPIClient.upload(token.getToken(), inputStream, fileName));
    }

    @PatchMapping("/stream/{fileName}")
    @ResponseBody
    public ResponseEntity stream(InputStream inputStream,
                                 @PathVariable("fileName") String fileName,
                                 @RequestParam(value = "chunk", required = false) String chunk,
                                 @RequestParam(value = "uploadId", required = false) String uploadId) throws IOException {
        Token token = tsdFileAPIClient.getToken(TokenType.IMPORT);
        Chunk result;
        if (StringUtils.isEmpty(uploadId)) { // new upload
            result = tsdFileAPIClient.initializeResumableUpload(token.getToken(), inputStream.readAllBytes(), fileName);
        } else if ("end".equalsIgnoreCase(chunk)) { // finalizing upload
            result = tsdFileAPIClient.finalizeResumableUpload(token.getToken(), uploadId);
        } else { // uploading an intermediate chunk
            result = tsdFileAPIClient.uploadChunk(token.getToken(), Long.parseLong(chunk), inputStream.readAllBytes(), uploadId);
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/resumables")
    @ResponseBody
    public ResponseEntity reumables() {
        Token token = tsdFileAPIClient.getToken(TokenType.IMPORT);
        return ResponseEntity.ok(tsdFileAPIClient.getResumableUploads(token.getToken()));
    }

    @DeleteMapping("/resumables")
    @ResponseBody
    public ResponseEntity reumables(@RequestParam(value = "uploadId", required = false) String uploadId) {
        Token token = tsdFileAPIClient.getToken(TokenType.IMPORT);
        return ResponseEntity.ok(tsdFileAPIClient.deleteResumableUpload(token.getToken(), uploadId));
    }

}
