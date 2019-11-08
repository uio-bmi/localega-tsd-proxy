package no.uio.ifi.localegas3proxy.s3.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@NoArgsConstructor
@AllArgsConstructor
@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Error")
public class ErrorResponse {

    @XmlElement(name = "Code")
    private String code;

    @XmlElement(name = "Message")
    private String message;

    @XmlElement(name = "Resource")
    private String resource;

    @XmlElement(name = "RequestId")
    private String requestId;

}
