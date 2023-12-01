package no.elixir.fega.ltp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;
import java.util.List;

/**
 * POJO for CEGA credentials.
 */
@ToString
@Data
public class Credentials {

    @JsonProperty("passwordHash")
    private String passwordHash;

    @JsonProperty("sshPublicKey")
    private List<String> publicKey;

}
