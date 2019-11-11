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
public class BucketsEntry {

    @XmlElement(name = "Name")
    private String name;

    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlElement(name = "CreationDate")
    private Instant creationDate;

}
