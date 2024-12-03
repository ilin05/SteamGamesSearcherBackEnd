package com.steamgamessearcherbackend.service;
import com.steamgamessearcherbackend.entities.Game;

import java.io.IOException;
import java.util.List;

public interface ElasticSearchService {

    public List<Game> searchGamesByTitle(String query) throws IOException;

    public List<Game> getAllGamesWithScroll(int pageSize) throws IOException;

    public List<Game> searchGamesByTags(String query) throws IOException;

    public List<Game> searchGamesByDescription(String query) throws IOException;

    public List<Game> comprehensiveSearch(String title, String tags, String description, String supportLanguages, Double lowestPrice, Double highestPrice, Boolean winSupport, Boolean linuxSupport, Boolean macSupport) throws IOException;
}
