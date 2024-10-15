package com.steamgamessearcherbackend.entities;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class Recommendation {
    private Integer reviewId;
    private Integer appId;
    private Integer helpful;
    private Integer funny;
    private Double hours;
    private boolean isRecommend;
}
