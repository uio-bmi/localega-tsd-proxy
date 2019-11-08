package no.uio.ifi.localegas3proxy.s3.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "ListBucketResult")
public class ListBucketResult {

    @XmlElement(name = "Name")
    private String name;

    @XmlElement(name = "Prefix")
    private String prefix;

    @XmlElement(name = "KeyCount")
    private Integer keyCount;

    @XmlElement(name = "MaxKeys")
    private Integer maxKeys;

    @XmlElement(name = "Delimiter")
    private String delimiter;

    @XmlElement(name = "IsTruncated")
    private Boolean isTruncated = Boolean.FALSE;

    @XmlElement(name = "Contents")
    private List<ContentsEntry> objects = new ArrayList<>();

    @XmlElement(name = "CommonPrefixes")
    private List<CommonPrefix> commonPrefixes = new ArrayList<>();

}
