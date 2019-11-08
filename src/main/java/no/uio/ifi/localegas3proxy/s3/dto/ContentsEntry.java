package no.uio.ifi.localegas3proxy.s3.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class ContentsEntry {

    private static final String STORAGE_CLASS_STANDARD = "STANDARD";
    private static final String STORAGE_CLASS_REDUCED_REDUNDANCY = "REDUCED_REDUNDANCY";

    @XmlElement(name = "Key")
    private String key;

    @XmlElement(name = "LastModified")
    private Instant lastModified;

    @XmlElement(name = "ETag")
    private String eTag;

    @XmlElement(name = "Size")
    private Integer size;

    @XmlElement(name = "StorageClass")
    private String storageClass = STORAGE_CLASS_STANDARD;

}
