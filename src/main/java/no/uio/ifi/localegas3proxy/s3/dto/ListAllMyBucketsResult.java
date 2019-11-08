package no.uio.ifi.localegas3proxy.s3.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "ListAllMyBucketsResult")
public class ListAllMyBucketsResult {

    @XmlElement(name = "Owner")
    private Owner owner;

    @XmlElementWrapper(name = "Buckets")
    @XmlElement(name = "Bucket")
    private List<BucketsEntry> buckets = new ArrayList<>();

}
