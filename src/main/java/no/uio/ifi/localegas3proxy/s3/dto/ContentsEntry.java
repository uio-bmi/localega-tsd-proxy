package no.uio.ifi.localegas3proxy.s3.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.uio.ifi.localegas3proxy.s3.util.InstantXmlAdapter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class ContentsEntry {

    public static final String STORAGE_CLASS_STANDARD = "STANDARD";
    public static final String STORAGE_CLASS_REDUCED_REDUNDANCY = "REDUCED_REDUNDANCY";

    @XmlElement(name = "Key")
    private String key;

    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlElement(name = "LastModified")
    private Instant lastModified;

    @XmlElement(name = "ETag")
    private String eTag;

    @XmlElement(name = "Size")
    private Integer size;

    @XmlElement(name = "StorageClass")
    private String storageClass;

}
