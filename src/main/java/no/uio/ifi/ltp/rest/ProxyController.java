package no.uio.ifi.ltp.rest;

import lombok.extern.slf4j.Slf4j;
import no.uio.ifi.tc.TSDFileAPIClient;
import no.uio.ifi.tc.model.TokenType;
import no.uio.ifi.tc.model.pojo.Chunk;
import no.uio.ifi.tc.model.pojo.ResumableUpload;
import no.uio.ifi.tc.model.pojo.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;

@SuppressWarnings("rawtypes")
@Slf4j
@RestController
public class ProxyController {

    @Autowired
    private TSDFileAPIClient tsdFileAPIClient;

    @PatchMapping("/stream/{fileName}")
    public ResponseEntity stream(InputStream inputStream,
                                 @PathVariable("fileName") String fileName,
                                 @RequestParam(value = "uploadId", required = false) String uploadId,
                                 @RequestParam(value = "chunk", required = false) String chunk,
                                 @RequestParam(value = "fileSize", required = false) String fileSize,
                                 @RequestParam(value = "md5") String md5) throws IOException {
        Token token = tsdFileAPIClient.getToken(TokenType.IMPORT);

        byte[] chunkBytes = inputStream.readAllBytes();

        // new upload
        if (StringUtils.isEmpty(uploadId)) {
            Chunk response = tsdFileAPIClient.initializeResumableUpload(token.getToken(), chunkBytes, fileName);
            return validateChunkChecksum(token, response, md5);
        }

        // finalizing upload
        if ("end".equalsIgnoreCase(chunk)) {
            return ResponseEntity.ok(tsdFileAPIClient.finalizeResumableUpload(token.getToken(), uploadId));
        }

        // uploading an intermediate chunk
        Chunk response = tsdFileAPIClient.uploadChunk(token.getToken(), Long.parseLong(chunk), chunkBytes, uploadId);
        return validateChunkChecksum(token, response, md5);
    }

    private ResponseEntity validateChunkChecksum(Token token, Chunk response, String md5) {
        ResumableUpload resumableUpload = tsdFileAPIClient.getResumableUpload(token.getToken(), response.getId()).orElseThrow();
        if (!md5.equalsIgnoreCase(resumableUpload.getMd5Sum())) {
            tsdFileAPIClient.deleteResumableUpload(token.getToken(), response.getId());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Checksum mismatch. Resumable upload interrrupted and can't be resumed. Please, re-upload the whole file.");
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/resumables")
    public ResponseEntity getResumables(@RequestParam(value = "uploadId", required = false) String uploadId) {
        Token token = tsdFileAPIClient.getToken(TokenType.IMPORT);
        if (StringUtils.isEmpty(uploadId)) {
            return ResponseEntity.ok(tsdFileAPIClient.getResumableUploads(token.getToken()));
        } else {
            return ResponseEntity.ok(tsdFileAPIClient.getResumableUpload(token.getToken(), uploadId));
        }
    }

    @DeleteMapping("/resumables")
    public ResponseEntity deleteResumable(@RequestParam(value = "uploadId") String uploadId) {
        Token token = tsdFileAPIClient.getToken(TokenType.IMPORT);
        return ResponseEntity.ok(tsdFileAPIClient.deleteResumableUpload(token.getToken(), uploadId));
    }

}
