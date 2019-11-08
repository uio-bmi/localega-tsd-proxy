package no.uio.ifi.localegas3proxy.s3.rest;

import lombok.extern.slf4j.Slf4j;
import no.uio.ifi.localegas3proxy.backend.BackendStorage;
import no.uio.ifi.localegas3proxy.s3.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController("/")
public class S3Controller {

    @Autowired
    private BackendStorage backendStorage;

    @RequestMapping(path = "**", produces = MediaType.APPLICATION_XML_VALUE)
    @ResponseBody
    public ResponseEntity get(HttpServletRequest request) {
        String username = String.valueOf(request.getAttribute("JWT_SUBJECT"));
        String requestURI = request.getRequestURI();
        if ("/".equals(requestURI)) {
            Collection<String> buckets = backendStorage.listAllMyBuckets(username);
            ListAllMyBucketsResult listAllMyBucketsResult = new ListAllMyBucketsResult(new Owner(username, username), buckets.stream().map(b -> new BucketsEntry(b, null)).collect(Collectors.toList()));
            return new ResponseEntity<>(listAllMyBucketsResult, HttpStatus.OK);
        } else {
            Collection<String> content = backendStorage.listBucket(username, requestURI);
            List<ContentsEntry> contentsEntries = content.stream().map(c -> new ContentsEntry(c, null, null, 42, null)).collect(Collectors.toList());
            ListBucketResult listBucketResult = new ListBucketResult("LocalEGA", "", 0, 0, "/", false, contentsEntries, Collections.emptyList());
            return new ResponseEntity<>(listBucketResult, HttpStatus.OK);
        }
    }

}
