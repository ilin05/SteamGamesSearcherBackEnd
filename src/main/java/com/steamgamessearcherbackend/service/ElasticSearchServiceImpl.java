package com.steamgamessearcherbackend.service;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.Time;
import co.elastic.clients.elasticsearch.core.ScrollResponse;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.TotalHits;
import co.elastic.clients.elasticsearch.core.search.TotalHitsRelation;
import co.elastic.clients.elasticsearch.core.ScrollRequest;
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
    public List<Game> searchGamesByTitle(String query) throws IOException {
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


    @Override
    public List<Game> getAllGamesWithScroll(int pageSize) throws IOException {
        List<Game> results = new ArrayList<>();

        // Initial search request
        SearchRequest searchRequest = new SearchRequest.Builder()
                .index("games")
                .size(pageSize)
                .scroll(Time.of(t -> t.time("1m")))
                .build();

        SearchResponse<Game> searchResponse = elasticsearchClient.search(searchRequest, Game.class);
        String scrollId = searchResponse.scrollId();

        while (searchResponse.hits().hits().size() > 0) {
            for (Hit<Game> hit : searchResponse.hits().hits()) {
                results.add(hit.source());
            }

            // Subsequent scroll requests
            ScrollRequest scrollRequest = new ScrollRequest.Builder()
                    .scrollId(scrollId)
                    .scroll(Time.of(t -> t.time("1m")))
                    .build();

            ScrollResponse<Game> scrollResponse = elasticsearchClient.scroll(scrollRequest, Game.class);
            scrollId = scrollResponse.scrollId();
            searchResponse = new SearchResponse.Builder<Game>()
                    .hits(scrollResponse.hits())
                    .scrollId(scrollResponse.scrollId())
                    .took(scrollResponse.took())
                    .timedOut(scrollResponse.timedOut())
                    .shards(scrollResponse.shards())
                    .build();
        }

        // Clear scroll context
        String finalScrollId = scrollId;
        elasticsearchClient.clearScroll(c -> c.scrollId(finalScrollId));

        return results;
    }

    @Override
    public List<Game> searchGamesByTags(String query) throws IOException {
        List<Game> results = new ArrayList<>();
        SearchRequest searchRequest = new SearchRequest.Builder()
                .index("games")
                .query(q-> q.match(m-> m.field("tags").query(query)))
                .build();
        SearchResponse<Game> searchResponse = elasticsearchClient.search(searchRequest, Game.class);
        for(Hit<Game> hit : searchResponse.hits().hits()) {
            results.add(hit.source());
        }
        return results;
    }

    @Override
    public List<Game> searchGamesByDescription(String query) throws IOException {
        List<Game> results = new ArrayList<>();
        SearchRequest searchRequest = new SearchRequest.Builder()
                .index("games")
                .query(q -> q.bool(b -> b
                        .must(m -> m.match(t -> t.field("tags").query(query).fuzziness("AUTO")))
                        .must(m -> m.match(d -> d.field("description").query(query).fuzziness("AUTO")))
                        .must(m -> m.match(t -> t.field("title").query(query).fuzziness("AUTO")))
                ))
                .size(10)
                .build();
//                .query(q-> q.match(m-> m.field("description").query(query)))
        SearchResponse<Game> searchResponse = elasticsearchClient.search(searchRequest, Game.class);
        for(Hit<Game> hit : searchResponse.hits().hits()) {
            results.add(hit.source());
        }
        return results;
    }

    @Override
    public List<Game> searchGamesByTitleAndTagsAndDescription(String title, String tags, String description) throws IOException {
        List<Game> results = new ArrayList<>();
        SearchRequest searchRequest = new SearchRequest.Builder()
                .index("games")
                .query(q -> q.bool(b -> b
                        .must(m -> m.match(t -> t.field("tags").query(tags).fuzziness("AUTO")))
                        .must(m -> m.match(d -> d.field("description").query(description).fuzziness("AUTO")))
                        .must(m -> m.match(t -> t.field("title").query(title).fuzziness("AUTO")))
                ))
                .size(10)
//                .query(q-> q.match(m-> m.field("title").query(title)))
//                .query(q-> q.match(m-> m.field("tags").query(tags)))
//                .query(q-> q.match(m-> m.field("description").query(description)))
                .build();
        SearchResponse<Game> searchResponse = elasticsearchClient.search(searchRequest, Game.class);
        for(Hit<Game> hit : searchResponse.hits().hits()) {
            results.add(hit.source());
        }
        return results;
    }
}
