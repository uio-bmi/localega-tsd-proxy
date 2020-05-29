package no.uio.ifi.ltp.aspects;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

import static no.uio.ifi.ltp.aspects.ProcessArgumentsAspect.EGA_USERNAME;
import static no.uio.ifi.ltp.aspects.ProcessArgumentsAspect.ELIXIR_ID;

/**
 * AOP aspect that maps EGA username with Elixir ID.
 */
@Slf4j
@Aspect
@Order(3)
@Component
public class CredentialsMappingAspect {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Stores EGA username to Elixir ID mapping.
     *
     * @param result Object returned by the proxied method.
     */
    @AfterReturning(pointcut = "execution(public * no.uio.ifi.ltp.controllers.rest.ProxyController.stream(..))", returning = "result")
    public void publishMessage(Object result) {
        String egaUsername = request.getAttribute(EGA_USERNAME).toString();
        String elixirID = request.getAttribute(ELIXIR_ID).toString();
        try {
            jdbcTemplate.update("insert into mapping values (?, ?)", egaUsername, elixirID);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

}
