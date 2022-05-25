package cn.voidnet.scoresystem.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.listener.ConsumerAwareListenerErrorHandler;
import org.springframework.kafka.support.converter.RecordMessageConverter;
import org.springframework.kafka.support.converter.StringJsonMessageConverter;

@Configuration
@Slf4j
public class KafkaConfig {
    @Bean
    public RecordMessageConverter converter() {
        ObjectMapper mapper = new ObjectMapper();
        return new StringJsonMessageConverter(mapper);
    }
    @Bean
    public ConsumerAwareListenerErrorHandler consumerAwareErrorHandler() {
        return (message, e, consumer) -> {
            log.error(e.toString());

            return "";
        };
    }

}
