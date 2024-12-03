package com.steamgamessearcherbackend;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.steamgamessearcherbackend.entities.Game;
import com.steamgamessearcherbackend.entities.GameForFrontEnd;
import com.steamgamessearcherbackend.mapper.UserMapper;
import com.steamgamessearcherbackend.repository.GameRepository;
import com.steamgamessearcherbackend.service.ElasticSearchService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class SteamGamesSearcherBackEndApplicationTests {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private ElasticSearchService elasticSearchService;

    @Test
    void testSearchByTitle() throws IOException {
        //String query = "Fortix 2";
        String query = "Galactic Bowling";
        List<Game> games = elasticSearchService.searchGamesByTitle(query);
        System.out.println("共查询到" + games.size() + "个结果");
        for(Game game : games){
            System.out.println(game);
        }
    }

    @Test
    void testShowFavoriteGames(){
        List<Game> games = userMapper.getUserFavorites(1);
        List<GameForFrontEnd> gamesForFrontEnd = new ArrayList<>();
        // System.out.println("共查询到" + games.size() + "个结果");
        for(Game game : games){
            // System.out.println(game);
            GameForFrontEnd gameForFrontEnd = new GameForFrontEnd();
            gameForFrontEnd.setAppId(game.getAppId());
            gameForFrontEnd.setTitle(game.getTitle());
            gameForFrontEnd.setReleasedDate(game.getReleasedDate());
            gameForFrontEnd.setWin(game.isWin());
            gameForFrontEnd.setMac(game.isMac());
            gameForFrontEnd.setLinux(game.isLinux());
            gameForFrontEnd.setPrice(game.getPrice());
            if(game.getTags() != null){
                gameForFrontEnd.setTags(List.of(game.getTags().split(", ")));
            }
            if(game.getSupportLanguage() != null){
                gameForFrontEnd.setSupportLanguage(List.of(game.getSupportLanguage().split(", ")));
            }
            gameForFrontEnd.setWebsite(game.getWebsite());
            gameForFrontEnd.setHeaderImage(game.getHeaderImage());
            gameForFrontEnd.setRecommendations(game.getRecommendations());
            gameForFrontEnd.setPositive(game.getPositive());
            gameForFrontEnd.setNegative(game.getNegative());
            gameForFrontEnd.setEstimatedOwners(game.getEstimatedOwners());
            if(game.getScreenshots() != null){
                gameForFrontEnd.setScreenshots(List.of(game.getScreenshots().split(", ")));
            }
            gameForFrontEnd.setDescription(game.getDescription());
            if(game.getMovies() != null){
                gameForFrontEnd.setMovies(List.of(game.getMovies().split(", ")));
            }
            gamesForFrontEnd.add(gameForFrontEnd);
        }
        System.out.println("共查询到" + gamesForFrontEnd.size() + "个结果");
        for(GameForFrontEnd game : gamesForFrontEnd){
            System.out.println(game);
        }
    }

    @Test
    void testSearchByTitleAndTagsAndDescription() throws IOException, InterruptedException {
        String query = "Galactic Bowling";
        String content = "Shoot vehicles, blow enemies with a special attack, protect your allies and ensure mission success!";
        Process process = Runtime.getRuntime().exec("python.exe src/main/python/deepseek.py \"" + content + "\"");
        InputStream inputStream = process.getInputStream();
        process.waitFor();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = -1;
        while((len = inputStream.read(buffer)) != -1){
            outputStream.write(buffer, 0, len);
        }
        System.out.println(outputStream);
        List<Game> games = elasticSearchService.comprehensiveSearch(query, outputStream.toString(), query + outputStream.toString(), null, null, null, null, null, null);
        List<GameForFrontEnd> gamesForFrontEnd = new ArrayList<>();
        // System.out.println("共查询到" + games.size() + "个结果");
        for(Game game : games){
            // System.out.println(game);
            GameForFrontEnd gameForFrontEnd = new GameForFrontEnd();
            gameForFrontEnd.setAppId(game.getAppId());
            gameForFrontEnd.setTitle(game.getTitle());
            gameForFrontEnd.setReleasedDate(game.getReleasedDate());
            gameForFrontEnd.setWin(game.isWin());
            gameForFrontEnd.setMac(game.isMac());
            gameForFrontEnd.setLinux(game.isLinux());
            gameForFrontEnd.setPrice(game.getPrice());
            if(game.getTags() != null){
                gameForFrontEnd.setTags(List.of(game.getTags().split(", ")));
            }
            if(game.getSupportLanguage() != null){
                gameForFrontEnd.setSupportLanguage(List.of(game.getSupportLanguage().split(", ")));
            }
            gameForFrontEnd.setWebsite(game.getWebsite());
            gameForFrontEnd.setHeaderImage(game.getHeaderImage());
            gameForFrontEnd.setRecommendations(game.getRecommendations());
            gameForFrontEnd.setPositive(game.getPositive());
            gameForFrontEnd.setNegative(game.getNegative());
            gameForFrontEnd.setEstimatedOwners(game.getEstimatedOwners());
            if(game.getScreenshots() != null){
                gameForFrontEnd.setScreenshots(List.of(game.getScreenshots().split(", ")));
            }
            gameForFrontEnd.setDescription(game.getDescription());
            if(game.getMovies() != null){
                gameForFrontEnd.setMovies(List.of(game.getMovies().split(", ")));
            }
            gamesForFrontEnd.add(gameForFrontEnd);
        }
        System.out.println("共查询到" + gamesForFrontEnd.size() + "个结果");
        for(GameForFrontEnd game : gamesForFrontEnd){
            System.out.println(game);
        }
    }

    @Test
    void testPythonScript() throws IOException, InterruptedException {
        Process process = Runtime.getRuntime().exec("python.exe src/main/python/deepseek.py");
        InputStream inputStream = process.getInputStream();
        process.waitFor();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = -1;
        while((len = inputStream.read(buffer)) != -1){
            outputStream.write(buffer, 0, len);
        }
        System.out.println(outputStream);
        List<Game> games = elasticSearchService.searchGamesByTags(outputStream.toString());
        System.out.println("共查询到" + games.size() + "个结果");
        for(Game game : games){
            System.out.println(game);
        }
    }

    @Test
    void testGetAllGamesWithScroll() throws IOException {
        List<Game> games = elasticSearchService.getAllGamesWithScroll(1000);
        System.out.println("共查询到" + games.size() + "个结果");
//        for(Game game : games){
//            System.out.println(game);
//        }
    }

    @Test
    void testFindAllTags() throws IOException {
        List<Game> games = elasticSearchService.getAllGamesWithScroll(1000);
        System.out.println("共查询到" + games.size() + "个结果");
        Map<String, Integer> tagMap = new java.util.HashMap<>();
        List<String> tags = new ArrayList<>();
        for(Game game : games){
            //System.out.println(game.getTags());
            if(game.getTags() == null){
                continue;
            }
            String[] parts = game.getTags().split(", ");
            for(String part : parts){
                if(!tags.contains(part)){
                    tags.add(part);
                    tagMap.put(part, 1);
                }else{
                    tagMap.replace(part, tagMap.get(part) + 1);
                }
            }
        }
        tags.sort((tag1, tag2) -> tagMap.get(tag2) - tagMap.get(tag1));
        System.out.println("repository中，游戏数目为：" + gameRepository.count());
        System.out.println("共查询到" + tags.size() + "个tag");
        for(String tag : tags){
            //System.out.println(tag);
            System.out.println(tag + ": " + tagMap.get(tag));
        }
        System.out.println(tags);
    }

    @Test
    void testFindAllLanguages() throws IOException {
        List<Game> games = elasticSearchService.getAllGamesWithScroll(1000);
        System.out.println("共查询到" + games.size() + "个结果");
        Map<String, Integer> languageMap = new java.util.HashMap<>();
        List<String> languages = new ArrayList<>();
        for(Game game : games){
            //System.out.println(game.getlanguages());
            if(game.getSupportLanguage() == null){
                continue;
            }
            String[] parts = game.getSupportLanguage().split(", ");
            for(String part : parts){
                if(!languages.contains(part)){
                    languages.add(part);
                    languageMap.put(part, 1);
                }else{
                    languageMap.replace(part, languageMap.get(part) + 1);
                }
            }
        }
        languages.sort((language1, language2) -> languageMap.get(language2) - languageMap.get(language1));
        System.out.println("repository中，游戏数目为：" + gameRepository.count());
        System.out.println("共查询到" + languages.size() + "个language");
        for(String language : languages){
            //System.out.println(language);
            System.out.println(language + ": " + languageMap.get(language));
        }
        System.out.println(languages);
    }

    @Test
    void contextLoads() {
        gameRepository.deleteAll();
        List<Game> gameList = userMapper.getAllGames();
        int batchSize = 5000;
        for (int i = 0; i < gameList.size(); i += batchSize) {
            List<Game> batchList = gameList.subList(i, Math.min(i + batchSize, gameList.size()));
            Iterable<Game> games = gameRepository.saveAll(batchList);
            gameRepository.saveAll(games);
            System.out.println("已存储" + (i + batchSize) + "个游戏");
        }
//        Iterable<Game> games = gameRepository.saveAll(gameList);
//        gameRepository.saveAll(games);
//        System.out.println("ListSize:");
//        System.out.println(gameList.size());
//        int i = 0;
//        for(Game game : gameList){
//            i++;
//            if(i%1000 == 0){
//                System.out.println(i);
//            }
//            System.out.println(game);
//            if(game.getMovies() == null){
//                game.setMovies("");
//            }
////            if(i == 5000){
////                System.out.println("size of gameList has reached 5000");
////                break;
////            }
//            //System.out.println(game);
//
//            break;
//        }
//        System.out.println("RepositorySize:");
//        System.out.println(gameRepository.count());
    }

    @Test
    void testFindByTitle(){
        List<Game> gameList = gameRepository.findByTitle("Galactic Bowling");
        System.out.println("共查询到" + gameList.size() + "个结果");
        System.out.println("-----------------");
        for(Game game : gameList){
            System.out.println(game);
        }
    }

    @Test
    void checkGameRepository(){
        System.out.println(gameRepository.count());
//        List<Game> gameList = (List<Game>)gameRepository.findAll();
//        for(Game game : gameList){
//            System.out.println(game);
//        }
    }

    @Test
    void storeGamesIntoMySQL(){
        ObjectMapper objectMapper = new ObjectMapper();
        try{
            JsonNode rootNode = objectMapper.readTree(new File("src/main/resources/static/games.json"));
            Iterator<String> keys = rootNode.fieldNames();
            int i = 0;
            for(JsonNode node : rootNode){
                //System.out.println(node.get("name").asText());
                System.out.println(node);
                Game game = new Game();
                game.setAppId(Integer.parseInt(keys.next()));
                game.setTitle(node.get("name").asText());
                game.setReleasedDate(node.get("release_date").asText());
                System.out.println("hello3");
                game.setWin(node.get("windows").asBoolean());
                game.setMac(node.get("mac").asBoolean());
                game.setLinux(node.get("linux").asBoolean());
                game.setPrice(node.get("price").asDouble());
                JsonNode tagsNode = node.get("tags");
                if(tagsNode != null && tagsNode.isObject()){
                    StringBuilder tags = new StringBuilder();
                    Iterator<Map.Entry<String, JsonNode>> fields = tagsNode.fields();
                    while(fields.hasNext()){
                        Map.Entry<String, JsonNode> field = fields.next();
                        tags.append(field.getKey());
                        if(fields.hasNext()){
                            tags.append(", ");
                        }
                    }
                    game.setTags(tags.toString());
                }
                JsonNode languagesNode = node.get("supported_languages");
                if(languagesNode.isArray()){
                    StringBuilder sb = new StringBuilder();
                    for(JsonNode languageNode : languagesNode){
                        sb.append(languageNode.asText());
                        sb.append(", ");
                    }
                    game.setSupportLanguage(sb.toString());
                }
                JsonNode screenshotsNode = node.get("screenshots");
                if(screenshotsNode.isArray()){
                    StringBuilder sb = new StringBuilder();
                    for(JsonNode screenshotNode : screenshotsNode){
                        sb.append(screenshotNode.asText());
                        sb.append(", ");
                    }
                    game.setScreenshots(sb.toString());
                }
                JsonNode moviesNode = node.get("movies");
                if(moviesNode.isArray() && !moviesNode.isEmpty()){
                    StringBuilder sb = new StringBuilder();
                    for(JsonNode movieNode : moviesNode){
                        sb.append(movieNode.asText());
                        sb.append(", ");
                    }
                    game.setMovies(sb.toString());
                }
                game.setWebsite(node.get("website").asText());
                game.setHeaderImage(node.get("header_image").asText());
                game.setRecommendations(node.get("recommendations").asInt());
                game.setPositive(node.get("positive").asInt());
                game.setNegative(node.get("negative").asInt());
                String[] parts = node.get("estimated_owners").asText().split(" - ");
                game.setEstimatedOwners((Integer.parseInt(parts[1]) + Integer.parseInt(parts[0])) / 2);
                game.setDescription(node.get("detailed_description").asText());
                // System.out.println(node.get("detailed_description").asText().length() > 65535);
                userMapper.storeGame(game);
                // games.add(game);
                //System.out.println(game);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
