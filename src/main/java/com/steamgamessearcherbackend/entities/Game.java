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
    private String releaseDate;
    private boolean winSupport;
    private boolean macSupport;
    private boolean linuxSupport;
    private Double price;
    private String tags;
    private String supportLanguage;
    private String website;
    private String headerImage;
    private Integer recommendations;
    private Integer positive;
    private Integer negative;
    private Integer estimatedOwners;
    private String screenshots;
    private String description;
}
