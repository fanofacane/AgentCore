package com.sky.AgentCore;

import org.dromara.x.file.storage.spring.EnableFileStorage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableFileStorage
@SpringBootApplication
public class AgentCoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(AgentCoreApplication.class, args);
    }

}
