package com.steamgamessearcherbackend.entities;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class Game {
    private Integer appId;
    private String title;
    private Date releaseDate;
    private boolean winSupport;
    private boolean macSupport;
    private boolean linuxSupport;
    private Double price;
    private String rating;
    private String tags;
    private String description;
}
