package no.uio.ifi.localegas3proxy.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@AllArgsConstructor
@Data
public class BackendFile {

    private String filename;
    private Instant modified;
    private Integer size;

}
