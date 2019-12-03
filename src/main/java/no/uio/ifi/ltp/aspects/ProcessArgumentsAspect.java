package no.uio.ifi.ltp.aspects;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.amqp.support.AmqpHeaders.USER_ID;

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

    @SuppressWarnings("rawtypes")
    @Around("execution(public * no.uio.ifi.ltp.rest.ProxyController.stream(..))")
    public Object replaceFileName(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] arguments = joinPoint.getArgs();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] parameterNames = signature.getParameterNames();
        Class[] parameterTypes = signature.getParameterTypes();
        for (int i = 0; i < arguments.length; i++) {
            if (parameterTypes[i].equals(String.class)) {
                switch (parameterNames[i]) {
                    case FILE_NAME:
                        arguments[i] = getFullFileName(arguments[i].toString());
                        request.setAttribute(FILE_NAME, arguments[i]);
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
    }

    private String getFullFileName(String fileName) {
        String elixirIdentity = request.getAttribute(USER_ID).toString();
        int atIndex = elixirIdentity.lastIndexOf("@");
        String prefix = elixirIdentity.substring(0, atIndex);
        return prefix + "-" + fileName;
    }

}
