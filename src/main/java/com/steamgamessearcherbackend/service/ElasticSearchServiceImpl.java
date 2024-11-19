package com.steamgamessearcherbackend.service;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.steamgamessearcherbackend.entities.Game;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ElasticSearchServiceImpl implements ElasticSearchService {

    private final ElasticsearchClient elasticsearchClient;

    public ElasticSearchServiceImpl(ElasticsearchClient elasticsearchClient) {
        this.elasticsearchClient = elasticsearchClient;
    }

    @Override
    public List<Game> searchGames(String query) throws IOException {
        List<Game> results = new ArrayList<>();
        SearchRequest searchRequest = new SearchRequest.Builder()
                .index("games")
                .query(q-> q.match(m-> m.field("title").query(query)))
                .build();
        SearchResponse<Game> searchResponse = elasticsearchClient.search(searchRequest, Game.class);
        for(Hit<Game> hit : searchResponse.hits().hits()) {
            results.add(hit.source());
        }
        return results;
    }
}
