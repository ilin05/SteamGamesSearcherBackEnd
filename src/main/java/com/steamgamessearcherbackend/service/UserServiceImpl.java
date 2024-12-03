package com.steamgamessearcherbackend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.steamgamessearcherbackend.mapper.UserMapper;
import com.steamgamessearcherbackend.service.*;
import com.steamgamessearcherbackend.entities.*;
import com.steamgamessearcherbackend.utils.ApiResult;
import org.springframework.boot.autoconfigure.batch.BatchProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
// import java.util.Map;
import java.util.Iterator;
import java.util.Map.Entry;

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
            System.out.println("hello2");
            String userName = user.getUserName();
            System.out.println(userName);
            int count2 = userMapper.checkUserName(userName);
            System.out.println(count2);
            if(count2 > 0){
                return ApiResult.failure("Username already in use");
                //throw new RuntimeException("User name already exists");
            }
            //System.out.println("count2: " + count2);

            System.out.println("hello3");
            userMapper.openAccount(userName, user.getPassword(), email);
            User newUser = userMapper.getUserByEmail(email);
            System.out.println("hello4");
            return ApiResult.success(newUser);
        }catch (Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ApiResult.failure("Error opening account");
        }
        //return null;
    }

    @Override
    public ApiResult deleteAccount(User user) {
        try{
            String email = user.getEmail();
            String password = user.getPassword();
            int count = userMapper.judgePassword(email, password);
            if(count != 1){
                return ApiResult.failure("邮箱或密码错误");
            }
            userMapper.deleteUser(email);
            return ApiResult.success(null);
        }catch (Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ApiResult.failure("Error deleting account");
        }
    }

    @Override
    public ApiResult modifyPassword(String email, String oldPassword, String newPassword) {
        try{
            int count = userMapper.judgePassword(email, oldPassword);
            if(count != 1){
                return ApiResult.failure("邮箱或密码错误");
            }
            userMapper.updatePassword(newPassword, email);
            return ApiResult.success(null);
        }catch (Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ApiResult.failure("Error modify password");
        }
    }

    @Override
    @Transactional
    public ApiResult modifyUserName(String email, String password, String newUserName) {
        try{
            int count = userMapper.judgePassword(email, password);
            if(count != 1){
                return ApiResult.failure("邮箱或密码错误");
            }
            count = userMapper.checkUserName(newUserName);
            if(count > 0){
                return ApiResult.failure("Username already in use");
            }
            userMapper.updateUserName(email, newUserName);
            return ApiResult.success(null);
        }catch (Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ApiResult.failure("Error modify user name");
        }
    }

    @Override
    public ApiResult userLogin(String email, String password) {
        try{
            int count = userMapper.judgePassword(email, password);
            if(count != 1){
                return ApiResult.failure("账户名或密码错误！");
            }else{
                Integer userId = userMapper.getUserId(email);
                return ApiResult.success(userId);
            }
        }catch (Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ApiResult.failure("Error judging user login");
        }
    }

    @Override
    public ApiResult userSearch(Integer userId, String query) {
        try{
            System.out.println("hello1");
            userMapper.saveSearchRecord(userId, query);
            Process process = Runtime.getRuntime().exec("python.exe src/main/python/deepseek.py \"" + query + "\"");
            InputStream inputStream = process.getInputStream();
            process.waitFor();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = -1;
            while((len = inputStream.read(buffer)) != -1){
                outputStream.write(buffer, 0, len);
            }
            String tags = outputStream.toString();
            List<Game> games = elasticSearchService.searchGamesByTitleAndTagsAndDescription(query, tags, query);
            // return ApiResult.success(games);

            // 如果下面的代码更方便前端展示数据的话，可以使用下面的代码
            List<GameForFrontEnd> gamesForFrontEnd = new ArrayList<>();
            for(Game game : games){
                GameForFrontEnd gameForFrontEnd = new GameForFrontEnd();
                gameForFrontEnd.setAppId(game.getAppId());
                gameForFrontEnd.setTitle(game.getTitle());
                gameForFrontEnd.setReleasedDate(game.getReleasedDate());
                gameForFrontEnd.setWinSupport(game.isWinSupport());
                gameForFrontEnd.setMacSupport(game.isMacSupport());
                gameForFrontEnd.setLinuxSupport(game.isLinuxSupport());
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
            return ApiResult.success(gamesForFrontEnd);
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
            List<GameForFrontEnd> gamesForFrontEnd = new ArrayList<>();
            for(Game game : games){
                GameForFrontEnd gameForFrontEnd = new GameForFrontEnd();
                gameForFrontEnd.setAppId(game.getAppId());
                gameForFrontEnd.setTitle(game.getTitle());
                gameForFrontEnd.setReleasedDate(game.getReleasedDate());
                gameForFrontEnd.setWinSupport(game.isWinSupport());
                gameForFrontEnd.setMacSupport(game.isMacSupport());
                gameForFrontEnd.setLinuxSupport(game.isLinuxSupport());
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
            return ApiResult.success(gamesForFrontEnd);
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
            tags.sort((tag1, tag2) -> tagMap.get(tag2) - tagMap.get(tag1));
            // 如果tags超过十个，则只保留前十个
            if(tags.size() > 10){
                tags = tags.subList(0, 10);
            }
            StringBuilder query = new StringBuilder();
            for(String tag : tags){
                query.append(tag).append(", ");
            }
            List<Game> recommendedGames = elasticSearchService.searchGamesByTags(query.toString());
            return ApiResult.success(recommendedGames);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        //return null;
    }
}
