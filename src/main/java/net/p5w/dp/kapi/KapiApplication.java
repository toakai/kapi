package net.p5w.dp.kapi;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = "net.p5w.dp.module")
public class KapiApplication {

    public static void main(String[] args) {
        SpringApplication.run(KapiApplication.class, args);
    }

}
