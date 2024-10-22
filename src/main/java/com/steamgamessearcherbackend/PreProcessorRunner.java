package com.steamgamessearcherbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class PreProcessorRunner {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(PreProcessorRunner.class, args);
        PreProcessor preProcessor = context.getBean(PreProcessor.class);
        preProcessor.processGames(args);
    }
}
