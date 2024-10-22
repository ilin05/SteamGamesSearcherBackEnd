package com.steamgamessearcherbackend;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.steamgamessearcherbackend.entities.Game;
import com.steamgamessearcherbackend.mapper.UserMapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

@Component
@MapperScan("com.steamgamessearcherbackend.mapper")
public class PreProcessor {
    public final UserMapper userMapper;

    @Autowired
    public PreProcessor(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public void processGames(String[] args) {
        ObjectMapper objectMapper = new ObjectMapper();
        try{
            JsonNode rootNode = objectMapper.readTree(new File("src/main/resources/static/games.json"));
            Iterator<String> keys = rootNode.fieldNames();
            int i = 0;
            for(JsonNode node : rootNode){
                //System.out.println(node.get("name").asText());
                //System.out.println(node);
                Game game = new Game();
                game.setAppId(Integer.parseInt(keys.next()));
                game.setTitle(node.get("name").asText());
                game.setReleaseDate(node.get("release_date").asText());
                System.out.println("hello3");
                game.setWinSupport(node.get("windows").asBoolean());
                game.setMacSupport(node.get("mac").asBoolean());
                game.setLinuxSupport(node.get("linux").asBoolean());
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
        //System.out.println("Hello, World!");
    }
}
