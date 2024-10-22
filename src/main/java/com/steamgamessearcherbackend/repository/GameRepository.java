package com.steamgamessearcherbackend.repository;

import com.steamgamessearcherbackend.entities.Game;
import org.springframework.stereotype.Repository;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

@Repository
public interface GameRepository extends ElasticsearchRepository<Game, Integer> {
    List<Game> findByTitle(String title);
}
