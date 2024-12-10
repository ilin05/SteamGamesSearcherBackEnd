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
    private boolean win;
    private boolean mac;
    private boolean linux;
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
    private List<String> developers;
    private List<String> publishers;
    private List<String> categories;
    private List<String> genres;
}
