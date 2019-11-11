package no.uio.ifi.localegas3proxy.s3.rest;

import lombok.extern.slf4j.Slf4j;
import no.uio.ifi.localegas3proxy.backend.BackendStorage;
import no.uio.ifi.localegas3proxy.backend.model.BackendFile;
import no.uio.ifi.localegas3proxy.s3.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController("/")
public class S3Controller {

    private static final String DEFAULT_DELIMITER = "/";

    @Autowired
    private BackendStorage backendStorage;

    @RequestMapping(path = "/", produces = MediaType.APPLICATION_XML_VALUE)
    @ResponseBody
    public ResponseEntity listBuckets(HttpServletRequest request) {
        String username = String.valueOf(request.getAttribute("JWT_SUBJECT"));
        Instant instant = new Date().toInstant();
        ListAllMyBucketsResult listAllMyBucketsResult = new ListAllMyBucketsResult(new Owner(username, username), Collections.singletonList(new BucketsEntry("LocalEGA", instant)));
        return new ResponseEntity<>(listAllMyBucketsResult, HttpStatus.OK);
    }

    @RequestMapping(path = "/LocalEGA", produces = MediaType.APPLICATION_XML_VALUE)
    @ResponseBody
    public ResponseEntity listFiles(HttpServletRequest request, @RequestParam(name = "prefix", required = false, defaultValue = "") String prefix) {
        String username = String.valueOf(request.getAttribute("JWT_SUBJECT"));
        Collection<BackendFile> files = backendStorage.list(username).stream().filter(f -> f.getFilename().startsWith(prefix)).peek(f -> f.setFilename(f.getFilename().replace(prefix, ""))).collect(Collectors.toList());
        List<ContentsEntry> contentsEntries = files.stream().filter(f -> !f.getFilename().contains(DEFAULT_DELIMITER)).map(f -> new ContentsEntry(f.getFilename(), f.getModified(), null, f.getSize(), null)).collect(Collectors.toList());
        List<CommonPrefix> commonPrefixes = files.stream().filter(f -> f.getFilename().contains(DEFAULT_DELIMITER)).map(f -> f.getFilename().split(DEFAULT_DELIMITER)[0]).distinct().map(CommonPrefix::new).collect(Collectors.toList());
        ListBucketResult listBucketResult = new ListBucketResult("LocalEGA", prefix, contentsEntries.size(), contentsEntries.size(), DEFAULT_DELIMITER, false, contentsEntries, commonPrefixes);
        return new ResponseEntity<>(listBucketResult, HttpStatus.OK);
    }

}
