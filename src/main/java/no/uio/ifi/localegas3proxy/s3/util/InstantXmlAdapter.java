package no.uio.ifi.localegas3proxy.s3.util;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

public class InstantXmlAdapter extends XmlAdapter<String, Instant> {

    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT;

    @Override
    public Instant unmarshal(String value) {
        return value != null ? formatter.parse(value, Instant::from) : null;
    }

    @Override
    public String marshal(Instant value) {
        return value != null ? formatter.format(value) : null;
    }

}
