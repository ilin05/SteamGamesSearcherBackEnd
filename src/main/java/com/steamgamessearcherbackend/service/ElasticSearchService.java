package com.steamgamessearcherbackend.service;
import com.steamgamessearcherbackend.entities.Game;

import java.io.IOException;
import java.util.List;

public interface ElasticSearchService {

    public List<Game> searchGames(String query) throws IOException;

}
