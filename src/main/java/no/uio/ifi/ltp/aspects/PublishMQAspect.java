package no.uio.ifi.ltp.aspects;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import no.uio.ifi.ltp.dto.EncryptedIntegrity;
import no.uio.ifi.ltp.dto.FileDescriptor;
import no.uio.ifi.ltp.dto.Operation;
import no.uio.ifi.tc.model.pojo.TSDFileAPIResponse;
import org.apache.http.entity.ContentType;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;
import java.util.UUID;

import static no.uio.ifi.ltp.aspects.ProcessArgumentsAspect.*;
import static org.springframework.amqp.support.AmqpHeaders.USER_ID;

@Slf4j
@Aspect
@Order(3)
@Component
public class PublishMQAspect {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private Gson gson;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${mq.exchange}")
    private String exchange;

    @Value("${mq.routing-key.files}")
    private String routingKeyFiles;

    @SuppressWarnings("unchecked")
    @AfterReturning(pointcut = "execution(public * no.uio.ifi.ltp.rest.ProxyController.stream(..))", returning = "result")
    public void publishMessage(Object result) {
        ResponseEntity<TSDFileAPIResponse> responseEntity = (ResponseEntity<TSDFileAPIResponse>) result;
        TSDFileAPIResponse apiResponse = responseEntity.getBody();
        if (!String.valueOf(Objects.requireNonNull(apiResponse).getStatusCode()).startsWith("20")) {
            log.error(apiResponse.getStatusText());
        }

        if (!"end".equalsIgnoreCase(request.getAttribute(CHUNK).toString())) {
            return;
        }

        FileDescriptor fileDescriptor = new FileDescriptor();
        fileDescriptor.setUser(request.getAttribute(USER_ID).toString());
        fileDescriptor.setFilePath(request.getAttribute(FILE_NAME).toString());
        fileDescriptor.setFileSize(Long.parseLong(request.getAttribute(FILE_SIZE).toString()));
        fileDescriptor.setFileLastModified(System.currentTimeMillis() / 1000);
        fileDescriptor.setOperation(Operation.UPLOAD.name().toLowerCase());
        fileDescriptor.setEncryptedIntegrity(new EncryptedIntegrity[]{
                new EncryptedIntegrity(MD5.toLowerCase(), request.getAttribute(MD5).toString())
        });
        publishMessage(fileDescriptor);
    }

    private void publishMessage(FileDescriptor fileDescriptor) {
        String json = gson.toJson(fileDescriptor);
        rabbitTemplate.convertAndSend(exchange, routingKeyFiles, json, m -> {
            m.getMessageProperties().setContentType(ContentType.APPLICATION_JSON.getMimeType());
            m.getMessageProperties().setCorrelationId(UUID.randomUUID().toString());
            return m;
        });
        log.info("Message published to {} exchange with routing key {}: {}", exchange, routingKeyFiles, json);
    }

}
