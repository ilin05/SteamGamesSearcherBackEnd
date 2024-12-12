package com.steamgamessearcherbackend.service;

import com.steamgamessearcherbackend.entities.*;
import com.steamgamessearcherbackend.utils.ApiResult;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

public interface UserService {

    // 创建用户
    ApiResult openAccount(User user);

    // 注销账号
    ApiResult deleteAccount(Integer userId);

    // 修改密码
    ApiResult modifyPassword(Integer userId, String oldPassword, String newPassword);

    // 修改用户名
    ApiResult modifyUserName(Integer userId, String password, String newUserName);

    // 用户登录
    ApiResult userLogin(String email, String password);

    // 用户搜索
    ApiResult userSearch(Integer userId, String query, List<String> tags, String supportLanguages, Double lowestPrice, Double highestPrice, Boolean winSupport, Boolean linuxSupport, Boolean macSupport, Boolean isTitle);

    // 收藏商品
    ApiResult favoriteGame(Integer userId, Integer appId);

    // 取消收藏
    ApiResult unfavoriteGame(Integer userId, Integer appId);

    // 获取用户收藏列表
    ApiResult getUserFavorites(Integer userId);

    // 猜你喜欢
    ApiResult recommendGames(Integer userId) throws IOException;

    ApiResult getGameDetail(Integer appId) throws IOException;

    ApiResult searchByTitle(String query) throws IOException;

    List<GameForFrontEnd> transferEntity(List<Game> games);

    ApiResult getUserInfo(Integer userId);

    ApiResult getGuidance(Integer appId) throws IOException;
}
