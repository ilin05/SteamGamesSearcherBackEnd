package com.steamgamessearcherbackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@MapperScan("com.steamgamessearcherbackend.mapper")
public class SteamGamesSearcherBackEndApplication {

    public static void main(String[] args) {
        SpringApplication.run(SteamGamesSearcherBackEndApplication.class, args);
    }

}
