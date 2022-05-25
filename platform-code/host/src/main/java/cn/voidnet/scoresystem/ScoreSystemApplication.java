package cn.voidnet.scoresystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class ScoreSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(ScoreSystemApplication.class, args);
    }

}
