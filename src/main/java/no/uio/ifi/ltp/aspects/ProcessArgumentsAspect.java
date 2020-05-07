package no.uio.ifi.ltp.aspects;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.amqp.support.AmqpHeaders.USER_ID;

/**
 * AOP aspect that processes HTTP request parameters.
 */
@Slf4j
@Aspect
@Order(2)
@Component
public class ProcessArgumentsAspect {

    public static final String FILE_NAME = "fileName";
    public static final String UPLOAD_ID = "uploadId";
    public static final String CHUNK = "chunk";
    public static final String FILE_SIZE = "fileSize";
    public static final String MD5 = "md5";

    @Autowired
    private HttpServletRequest request;

    /**
     * Converts HTTP request parameters to request attributes.
     *
     * @param joinPoint Join point referencing proxied method.
     * @return Either the object, returned by the proxied method, or HTTP error response.
     * @throws Throwable In case of error.
     */
    @SuppressWarnings("rawtypes")
    @Around("execution(public * no.uio.ifi.ltp.controllers.rest.ProxyController.stream(..))")
    public Object processArguments(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            Object[] arguments = joinPoint.getArgs();
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            String[] parameterNames = signature.getParameterNames();
            Class[] parameterTypes = signature.getParameterTypes();
            for (int i = 0; i < arguments.length; i++) {
                if (parameterTypes[i].equals(String.class)) {
                    switch (parameterNames[i]) {
                        case FILE_NAME:
                            request.setAttribute(FILE_NAME, arguments[i]);
                            arguments[i] = getFullFileName(arguments[i].toString());
                            break;
                        case UPLOAD_ID:
                            request.setAttribute(UPLOAD_ID, arguments[i]);
                            break;
                        case CHUNK:
                            request.setAttribute(CHUNK, arguments[i]);
                            break;
                        case FILE_SIZE:
                            request.setAttribute(FILE_SIZE, arguments[i]);
                            break;
                        case MD5:
                            request.setAttribute(MD5, arguments[i]);
                            break;
                    }
                }
            }
            return joinPoint.proceed(arguments);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    private String getFullFileName(String fileName) {
        String elixirIdentity = request.getAttribute(USER_ID).toString();
        if (elixirIdentity.contains("@")) {
            int atIndex = elixirIdentity.lastIndexOf("@");
            elixirIdentity = elixirIdentity.substring(0, atIndex);
        }
        return elixirIdentity + "-" + fileName;
    }

}
