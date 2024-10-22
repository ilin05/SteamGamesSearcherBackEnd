package com.steamgamessearcherbackend.entities;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;
import java.io.Serializable;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Data
@Document(indexName = "games")
public class Game implements Serializable{
    private static final long serialVersionUID = 42L;
    @Id
    @Field(type = FieldType.Integer)
    private Integer appId;

    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String title;

    private String releaseDate;
    private boolean winSupport;
    private boolean macSupport;
    private boolean linuxSupport;
    private Double price;

    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String tags;

    private String supportLanguage;
    private String website;
    private String headerImage;
    private Integer recommendations;
    private Integer positive;
    private Integer negative;
    private Integer estimatedOwners;
    private String screenshots;

    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String description;
}
