package no.uio.ifi.localegas3proxy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class LocalEGAS3ProxyApplication {

    public static void main(String[] args) {
        SpringApplication.run(LocalEGAS3ProxyApplication.class, args);
    }

}
