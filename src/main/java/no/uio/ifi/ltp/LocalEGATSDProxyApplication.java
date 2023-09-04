package no.uio.ifi.ltp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;


/**
 * Spring Boot main file containing the application entry-point and all necessary Spring beans configuration.
 */
@Slf4j
@EnableCaching
@SpringBootApplication
public class LocalEGATSDProxyApplication {
    public static void main(String[] args) {
        SpringApplication.run(LocalEGATSDProxyApplication.class, args);
    }
}
