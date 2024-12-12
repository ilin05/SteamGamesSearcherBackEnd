package com.steamgamessearcherbackend.service;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.Time;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.core.ScrollResponse;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.TotalHits;
import co.elastic.clients.elasticsearch.core.search.TotalHitsRelation;
import co.elastic.clients.elasticsearch.core.ScrollRequest;
import co.elastic.clients.json.JsonData;
import co.elastic.clients.util.ObjectBuilder;
import com.steamgamessearcherbackend.entities.Game;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

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
    public List<Game> comprehensiveSearch(String title, String tags, String description, String supportLanguages, Double lowestPrice, Double highestPrice, Boolean winSupport, Boolean linuxSupport, Boolean macSupport) throws IOException {
        System.out.println("title: " + title);
        System.out.println("tags: " + tags);
        System.out.println("description: " + description);
        System.out.println("supportLanguages: " + supportLanguages);
        System.out.println("lowestPrice: " + lowestPrice);
        System.out.println("highestPrice: " + highestPrice);
        System.out.println("winSupport: " + winSupport);
        System.out.println("linuxSupport: " + linuxSupport);
        System.out.println("macSupport: " + macSupport);
        List<Game> results = new ArrayList<>();
        // RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("age");
        SearchRequest searchRequest = new SearchRequest.Builder()
                .index("games")
                .query(q -> q.bool(b -> {
                    b.must(m -> m.match(t -> t.field("tags").query(tags).fuzziness("AUTO")));
                    b.must(m -> m.match(d -> d.field("description").query(description).fuzziness("AUTO")));
                    b.must(m -> m.match(t -> t.field("title").query(title).fuzziness("AUTO")));
                    if(!supportLanguages.isEmpty()) {
                        b.must(m -> m.match(t -> t.field("supportLanguage").query(supportLanguages).fuzziness("AUTO")));
                    }
                    if (lowestPrice != null) {
                        UntypedRangeQuery.Builder lowestPriceFilter = new UntypedRangeQuery.Builder();
                        lowestPriceFilter.field("price").gte(JsonData.of(lowestPrice)).lte(JsonData.of(highestPrice));
                        UntypedRangeQuery lowestPriceQuery = lowestPriceFilter.build();
                        b.must(lowestPriceQuery._toRangeQuery()._toQuery());
                        //b.must(m -> m.range((Function<RangeQuery.Builder, ObjectBuilder<RangeQuery>>) lowestPriceFilter.build()));
                    }
                    if (highestPrice != null) {
                        UntypedRangeQuery.Builder highestPriceFilter = new UntypedRangeQuery.Builder();
                        highestPriceFilter.field("price").lte(JsonData.of(highestPrice));
                        UntypedRangeQuery highestPriceQuery = highestPriceFilter.build();
                        b.must(highestPriceQuery._toRangeQuery()._toQuery());
                        //b.must(m -> m.range((Function<RangeQuery.Builder, ObjectBuilder<RangeQuery>>) highestPriceFilter.build()));
                    }
                    if(winSupport) {
                        b.must(m -> m.match(t -> t.field("win").query(true)));
                    }
                    if(linuxSupport) {
                        b.must(m -> m.match(t -> t.field("linux").query(true)));
                    }
                    if(macSupport) {
                        b.must(m -> m.match(t -> t.field("mac").query(true)));
                    }
                    return b;
                }))
//                .query(q-> q.match(m-> m.field("title").query(title)))
//                .query(q-> q.match(m-> m.field("tags").query(tags)))
//                .query(q-> q.match(m-> m.field("description").query(description)))
//                .query(q-> q.match(m-> m.field("supportLanguage").query(supportLanguages)))
                //.size(10)
                .build();
        SearchResponse<Game> searchResponse = elasticsearchClient.search(searchRequest, Game.class);
        for(Hit<Game> hit : searchResponse.hits().hits()) {
            results.add(hit.source());
        }
        return results;
    }

    @Override
    public List<Game> searchWithoutQuery(String tags, String description, String supportLanguages, Double lowestPrice, Double highestPrice, Boolean winSupport, Boolean linuxSupport, Boolean macSupport) throws IOException {
        System.out.println("tags: " + tags);
        System.out.println("description: " + description);
        System.out.println("supportLanguages: " + supportLanguages);
        System.out.println("lowestPrice: " + lowestPrice);
        System.out.println("highestPrice: " + highestPrice);
        System.out.println("winSupport: " + winSupport);
        System.out.println("linuxSupport: " + linuxSupport);
        System.out.println("macSupport: " + macSupport);
        List<Game> results = new ArrayList<>();
        // RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("age");
        SearchRequest searchRequest = new SearchRequest.Builder()
                .index("games")
                .query(q -> q.bool(b -> {
                    if(!Objects.equals(tags, "")) {
                        System.out.println("tags: " + tags);
                        b.must(m -> m.match(t -> t.field("tags").query(tags).fuzziness("AUTO")));
                    }
                    if(!Objects.equals(description, "")) {
                        System.out.println("description: " + description);
                        b.must(m -> m.match(d -> d.field("description").query(description).fuzziness("AUTO")));
                    }
                    if(!supportLanguages.isEmpty()) {
                        System.out.println("supportLanguages: " + supportLanguages);
                        b.must(m -> m.match(t -> t.field("supportLanguage").query(supportLanguages).fuzziness("AUTO")));
                    }
                    if (lowestPrice != null) {
                        UntypedRangeQuery.Builder lowestPriceFilter = new UntypedRangeQuery.Builder();
                        lowestPriceFilter.field("price").gte(JsonData.of(lowestPrice)).lte(JsonData.of(highestPrice));
                        UntypedRangeQuery lowestPriceQuery = lowestPriceFilter.build();
                        b.must(lowestPriceQuery._toRangeQuery()._toQuery());
                        //b.must(m -> m.range((Function<RangeQuery.Builder, ObjectBuilder<RangeQuery>>) lowestPriceFilter.build()));
                    }
                    if (highestPrice != null) {
                        UntypedRangeQuery.Builder highestPriceFilter = new UntypedRangeQuery.Builder();
                        highestPriceFilter.field("price").lte(JsonData.of(highestPrice));
                        UntypedRangeQuery highestPriceQuery = highestPriceFilter.build();
                        b.must(highestPriceQuery._toRangeQuery()._toQuery());
                        //b.must(m -> m.range((Function<RangeQuery.Builder, ObjectBuilder<RangeQuery>>) highestPriceFilter.build()));
                    }
                    if(winSupport) {
                        b.must(m -> m.match(t -> t.field("win").query(true)));
                    }
                    if(linuxSupport) {
                        b.must(m -> m.match(t -> t.field("linux").query(true)));
                    }
                    if(macSupport) {
                        b.must(m -> m.match(t -> t.field("mac").query(true)));
                    }
                    return b;
                }))
//                .query(q-> q.match(m-> m.field("title").query(title)))
//                .query(q-> q.match(m-> m.field("tags").query(tags)))
//                .query(q-> q.match(m-> m.field("description").query(description)))
//                .query(q-> q.match(m-> m.field("supportLanguage").query(supportLanguages)))
                //.size(10)
                .build();
        SearchResponse<Game> searchResponse = elasticsearchClient.search(searchRequest, Game.class);
        for(Hit<Game> hit : searchResponse.hits().hits()) {
            results.add(hit.source());
        }
        return results;
    }
}
