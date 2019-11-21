package no.uio.ifi.ltp;

import no.uio.ifi.tc.TSDFileAPIClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;

@EnableCaching
@SpringBootApplication
public class LocalEGATSDProxyApplication {

    @Value("${tsd.access-key}")
    private String tsdAccessKey;

    public static void main(String[] args) {
        SpringApplication.run(LocalEGATSDProxyApplication.class, args);
    }

    @Bean
    public TSDFileAPIClient tsdFileAPIClient() {
        return new TSDFileAPIClient.Builder().accessKey(tsdAccessKey).build();
    }

}
