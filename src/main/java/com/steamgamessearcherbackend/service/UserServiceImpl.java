package com.steamgamessearcherbackend.service;

import com.steamgamessearcherbackend.mapper.UserMapper;
import com.steamgamessearcherbackend.entities.*;
import com.steamgamessearcherbackend.utils.ApiResult;
import com.steamgamessearcherbackend.utils.HashUtils;
import com.steamgamessearcherbackend.utils.YouDaoTranslator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.io.*;
import java.util.*;
// import java.util.Map;


@Service
public class UserServiceImpl implements UserService{

    private final UserMapper userMapper;
    private final ElasticSearchService elasticSearchService;

    public UserServiceImpl(UserMapper userMapper, ElasticSearchService elasticSearchService) {
        this.userMapper = userMapper;
        this.elasticSearchService = elasticSearchService;
    }

    @Override
    @Transactional
    public ApiResult openAccount(User user) {
        try{
            String email = user.getEmail();
            int count = userMapper.checkEmail(email);
            if(count > 0){
                return ApiResult.failure("Email already in use");
                //throw new RuntimeException("Email already registered");
            }
            String userName = user.getUserName();
            System.out.println(userName);
            int count2 = userMapper.checkUserName(userName);
            System.out.println(count2);
            if(count2 > 0){
                return ApiResult.failure("Username already in use");
                //throw new RuntimeException("User name already exists");
            }
            //System.out.println("count2: " + count2);
            userMapper.openAccount(userName, HashUtils.sha256Hash(user.getPassword()), email);
            User newUser = userMapper.getUserByEmail(email);
            return ApiResult.success(newUser);
        }catch (Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ApiResult.failure("Error opening account");
        }
        //return null;
    }

    @Override
    public ApiResult deleteAccount(Integer userId) {
        try{
            userMapper.deleteUserFavorites(userId);
            userMapper.deleteUserSearchRecords(userId);
            userMapper.deleteUser(userId);
            return ApiResult.success(null);
        }catch (Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ApiResult.failure("Error deleting account");
        }
    }

    @Override
    public ApiResult modifyPassword(Integer userId, String oldPassword, String newPassword) {
        try{
            int count = userMapper.judgePassword(userId, HashUtils.sha256Hash(oldPassword));
            if(count != 1){
                return ApiResult.failure("密码错误");
            }
            userMapper.updatePassword(HashUtils.sha256Hash(newPassword), userId);
            return ApiResult.success(null);
        }catch (Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ApiResult.failure("Error modify password");
        }
    }

    @Override
    @Transactional
    public ApiResult modifyUserName(Integer userId, String newUserName) {
        try{
            int count = userMapper.checkUserName(newUserName);
            if(count > 0){
                return ApiResult.failure("Username already in use");
            }
            userMapper.updateUserName(userId, newUserName);
            return ApiResult.success(null);
        }catch (Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ApiResult.failure("Error modify user name");
        }
    }

    @Override
    public ApiResult userLogin(String email, String password) {
        try{
            Integer userId = userMapper.getUserId(email);
            if(userId == null){
                return ApiResult.failure("账户名或密码错误！");
            }
            int count = userMapper.judgePassword(userId, HashUtils.sha256Hash(password));
            if(count != 1){
                return ApiResult.failure("账户名或密码错误！");
            }else{
                return ApiResult.success(userId);
            }
        }catch (Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ApiResult.failure("Error judging user login");
        }
    }

    @Override
    public ApiResult userSearch(Integer userId, String query, List<String> tags, String supportLanguages, Double lowestPrice, Double highestPrice, Boolean winSupport, Boolean linuxSupport, Boolean macSupport, Boolean isTitle) {
        try{
            if(isTitle){
                if(!query.matches("^[a-zA-Z0-9\\s]+$")){
                    query = YouDaoTranslator.translate(query);
                }
                List<Game> games = elasticSearchService.searchGamesByTitle(query);
                // return ApiResult.success(games);
                // 如果下面的代码更方便前端展示数据的话，可以使用下面的代码
                List<GameForFrontEnd> gamesForFrontEnd = transferEntity(games);
                return ApiResult.success(gamesForFrontEnd);
            }else{
                List<Game> games = new ArrayList<>();
                String extractedTags;
                if(!query.isEmpty()){
                    if(!query.matches("^[a-zA-Z0-9\\s]+$")){
                        query = YouDaoTranslator.translate(query);
                    }
                    // System.out.println("hello1");
                    // 如果输入的不是英文的话，就调用有道翻译API进行翻译
                    // userMapper.saveSearchRecord(userId, query);
                    Process process = Runtime.getRuntime().exec("python.exe src/main/python/deepseek.py \"" + query + "\"");
                    InputStream inputStream = process.getInputStream();
                    process.waitFor();
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    int len = -1;
                    while((len = inputStream.read(buffer)) != -1){
                        outputStream.write(buffer, 0, len);
                    }
                    extractedTags = outputStream.toString();
                    for(String tag : tags){
                        if(!extractedTags.contains(tag)){
                            extractedTags += ", " + tag;
                        }
                    }
                    System.out.println("hello1");
                    games = elasticSearchService.comprehensiveSearch(query, extractedTags, query, supportLanguages, lowestPrice, highestPrice, winSupport, linuxSupport, macSupport);
                }else{
                    extractedTags = "";
                    for(String tag : tags){
                        extractedTags += tag + ", ";
                    }
                    games = elasticSearchService.searchWithoutQuery(extractedTags, extractedTags, supportLanguages, lowestPrice, highestPrice, winSupport, linuxSupport, macSupport);
                }
                if(!Objects.equals(extractedTags, "")){
                    userMapper.saveSearchRecord(userId, query, extractedTags);
                }
                // return ApiResult.success(games);
                // 如果下面的代码更方便前端展示数据的话，可以使用下面的代码
                List<GameForFrontEnd> gamesForFrontEnd = transferEntity(games);
                return ApiResult.success(gamesForFrontEnd);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ApiResult favoriteGame(Integer userId, Integer appId) {
        try{
            if(userMapper.checkFavorite(userId, appId) > 0){
                return ApiResult.failure("Game already in favorites");
            }
            userMapper.favoriteGame(userId, appId);
            return ApiResult.success(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ApiResult unfavoriteGame(Integer userId, Integer appId) {
        try{
            userMapper.unfavoriteGame(userId, appId);
            return ApiResult.success(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ApiResult getUserFavorites(Integer userId) {
        try{
            List<Game> games = userMapper.getUserFavorites(userId);
            // return ApiResult.success(games);

            // 如果下面的代码更方便前端展示数据的话，可以使用下面的代码
            // List<GameForFrontEnd> gamesForFrontEnd = transferEntity(games);
            List<HashMap<String, String>> results = new ArrayList<>();
            for(Game game : games){
                HashMap<String, String> result = new HashMap<>();
                result.put("appId", game.getAppId().toString());
                result.put("title", game.getTitle());
                result.put("headerImage", game.getHeaderImage());
                results.add(result);
            }
            return ApiResult.success(results);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ApiResult recommendGames(Integer userId) throws IOException {
        try {
            List<Game> userFavorites = userMapper.getUserFavorites(userId);
            List<String> tags = new ArrayList<>();
            Map<String, Integer> tagMap = new java.util.HashMap<>();
            for(Game game : userFavorites){
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

            List<String> searchTags = userMapper.getSearchTags(userId);
            for(String searchTag : searchTags){
                List<String> tempTags = Arrays.asList(searchTag.split(", "));
                for(String tempTag : tempTags){
                    if(!tags.contains(tempTag)){
                        tags.add(tempTag);
                        tagMap.put(tempTag, 1);
                    }else{
                        tagMap.replace(tempTag, tagMap.get(tempTag) + 1);
                    }
                }
            }
            List<Game> recommendedGames = new ArrayList<>();
            if(!tags.isEmpty()){
                tags.sort((tag1, tag2) -> tagMap.get(tag2) - tagMap.get(tag1));
                // 如果tags超过十个，则只保留前十个
                if(tags.size() > 10){
                    tags = tags.subList(0, 10);
                }
                StringBuilder query = new StringBuilder();
                for(String tag : tags){
                    query.append(tag).append(", ");
                }
                recommendedGames = elasticSearchService.searchGamesByTags(query.toString());
            } else {
                System.out.println("No tags");
                recommendedGames = userMapper.getTopHotGames();
            }
            List<GameForFrontEnd> gamesForFrontEnd = transferEntity(recommendedGames);
            return ApiResult.success(gamesForFrontEnd);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        //return null;
    }

    @Override
    public ApiResult getGameDetail(Integer appId) throws IOException {
        try {
            Game game = userMapper.getGameByAppId(appId);
            List<Game> games = new ArrayList<>();
            games.add(game);
            List<GameForFrontEnd> gamesForFrontEndList = transferEntity(games);
            GameForFrontEnd gameForFrontEnd = gamesForFrontEndList.get(0);
            return ApiResult.success(gameForFrontEnd);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // return null;
    }

    @Override
    public ApiResult searchByTitle(String query) throws IOException {
        try{
            if(!query.matches("^[a-zA-Z0-9\\s]+$")){
                query = YouDaoTranslator.translate(query);
            }
            List<Game> games = elasticSearchService.searchGamesByTitle(query);
            List<GameForFrontEnd> gamesForFrontEnd = transferEntity(games);
            return ApiResult.success(gamesForFrontEnd);
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<GameForFrontEnd> transferEntity(List<Game> games) {
        List<GameForFrontEnd> gamesForFrontEnd = new ArrayList<>();
        for(Game game : games){
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
                // 如果screenshots超过十个，则只保留前十个
                if(gameForFrontEnd.getScreenshots().size() > 10){
                    gameForFrontEnd.setScreenshots(gameForFrontEnd.getScreenshots().subList(0, 10));
                }
            }
            gameForFrontEnd.setDescription(game.getDescription());
            if(game.getMovies() != null){
                gameForFrontEnd.setMovies(List.of(game.getMovies().split(", ")));
            }
            if(game.getDevelopers() != null){
                gameForFrontEnd.setDevelopers(List.of(game.getDevelopers().split(", ")));
            }
            if(game.getPublishers() != null){
                gameForFrontEnd.setPublishers(List.of(game.getPublishers().split(", ")));
            }
            if(game.getCategories() != null){
                gameForFrontEnd.setCategories(List.of(game.getCategories().split(", ")));
            }
            if(game.getGenres() != null){
                gameForFrontEnd.setGenres(List.of(game.getGenres().split(", ")));
            }
            gamesForFrontEnd.add(gameForFrontEnd);
        }
        return gamesForFrontEnd;
    }

    @Override
    public ApiResult getUserInfo(Integer userId) {
        HashMap<String, String> userInfo = new HashMap<>();
        User user = userMapper.getUserInfo(userId);
        userInfo.put("userName", user.getUserName());
        userInfo.put("email", user.getEmail());
        return ApiResult.success(userInfo);
    }

    @Override
    public ApiResult getGuidance(Integer appId) throws IOException {
        try{
            Game game = userMapper.getGameByAppId(appId);
            String title = game.getTitle();
            String content = "The title: " + title + "The tags: " + game.getTags() + "; The description: " + game.getDescription() + "; The categories: " + game.getCategories() + "; The genres: " + game.getGenres();
            // 将content存入src/main/python/content.txt
            File file = new File("src/main/python/content.txt");
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(content);
            }
            Process process = Runtime.getRuntime().exec("python.exe src/main/python/getGuidance.py");
            process.waitFor();
            System.out.println("hello");
            File guidanceFile = new File("src/main/python/guidance.txt");
            StringBuilder guidance = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new FileReader(guidanceFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    guidance.append(line).append("\n");
                }
            }
            System.out.println(guidance.toString());
            return ApiResult.success(guidance.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
