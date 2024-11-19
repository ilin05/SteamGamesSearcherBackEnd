package com.steamgamessearcherbackend.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty("appId")
    @Id
    @Field(type = FieldType.Integer)
    private Integer appId;

    @JsonProperty("title")
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String title;

    @JsonProperty("releaseDate")
    private String releaseDate;

    @JsonProperty("winSupport")
    private boolean winSupport;

    @JsonProperty("macSupport")
    private boolean macSupport;

    @JsonProperty("linuxSupport")
    private boolean linuxSupport;

    @JsonProperty("price")
    private Double price;

    @JsonProperty("tags")
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String tags;

    @JsonProperty("supportLanguage")
    private String supportLanguage;

    @JsonProperty("website")
    private String website;

    @JsonProperty("headerImage")
    private String headerImage;

    @JsonProperty("recommendations")
    private Integer recommendations;

    @JsonProperty("positive")
    private Integer positive;

    @JsonProperty("negative")
    private Integer negative;

    @JsonProperty("estimatedOwners")
    private Integer estimatedOwners;

    @JsonProperty("screenshots")
    private String screenshots;

    @JsonProperty("description")
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String description;
}
