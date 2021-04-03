package demo.rabbit.retry.backoff.listener;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import demo.rabbit.retry.backoff.config.BindingConfiguration;
import demo.rabbit.retry.backoff.model.Request;
import java.io.IOException;
import java.io.Serializable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer;

@Slf4j
public class ObservableRejectAndDontRequeueRecoverer extends RejectAndDontRequeueRecoverer {
    private Runnable observer;


    private final RabbitTemplate template;

    private final ObjectMapper mapper;

    public ObservableRejectAndDontRequeueRecoverer(
        RabbitTemplate template, ObjectMapper mapper) {
        this.template = template;
        this.mapper = mapper;
    }

    @Override
    public void recover(Message message, Throwable cause) {
        if(observer != null) {
            observer.run();
        }

        publishToParkingLotQueue(message, cause);

        super.recover(message, cause);
    }
    
    public void setObserver(Runnable observer){
        this.observer = observer;
    }

    private void publishToParkingLotQueue(Message message, Throwable cause) {
        try {
            template.convertAndSend(BindingConfiguration.EXCHANGE_NAME,
                BindingConfiguration.PARKING_KEY, ParkingLot.builder()
                    .request(mapper.readValue(message.getBody(), Request.class).toString())
                    .problem(cause.getCause().getMessage())
                    .build(), processor -> {
                    // you can set failed information in header properties
                    processor.getMessageProperties().setHeader("priority",
                        message.getMessageProperties().getPriority());
                    return processor;
                });
        } catch (IOException e) {
            log.error("error parse {}", e.getMessage());
        }
    }

    @Getter
    @ToString
    @EqualsAndHashCode
    private static class ParkingLot implements Serializable {

        private static final long serialVersionUID = 5510074191983722944L;
        private final String problem;
        private final String request;

        @JsonCreator
        @lombok.Builder(builderClassName = "Builder")
        ParkingLot(@JsonProperty String problem, @JsonProperty String request) {
            this.problem = problem;
            this.request = request;
        }
    }
}
