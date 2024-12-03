package com.steamgamessearcherbackend.entities;

import lombok.*;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class GameForFrontEnd implements Serializable{
    private Integer appId;
    private String title;
    private String releasedDate;
    private boolean winSupport;
    private boolean macSupport;
    private boolean linuxSupport;
    private Double price;
    private List<String> tags;
    private List<String> supportLanguage;
    private String website;
    private String headerImage;
    private Integer recommendations;
    private Integer positive;
    private Integer negative;
    private Integer estimatedOwners;
    private List<String> screenshots;
    private String description;
    private List<String> movies;
}
