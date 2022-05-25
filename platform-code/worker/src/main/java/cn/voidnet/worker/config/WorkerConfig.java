package cn.voidnet.worker.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

@Configuration
@Slf4j
public class WorkerConfig {
    @Value("${score.worker.name:}")
    String workerName;

    static final String RANDOM_ID =getRandomId();
    static private String getRandomId(){
        var uuid =UUID.randomUUID().toString();
        return uuid.substring(uuid.length()-6);
    }


    @Bean
    public WorkerInfo getWorkerInfo() {
        var ip = "";
        try {
            var host = InetAddress.getLocalHost();
            ip = host.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return WorkerInfo
                .builder()
                .ip(ip)
                .name(StringUtils.hasText(workerName) ? workerName : RANDOM_ID)
                .build();
    }


}
