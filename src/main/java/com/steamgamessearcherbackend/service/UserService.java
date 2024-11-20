package com.steamgamessearcherbackend.service;

import com.steamgamessearcherbackend.entities.*;
import com.steamgamessearcherbackend.utils.ApiResult;

import java.io.IOException;

public interface UserService {

    // 创建用户
    ApiResult openAccount(User user);

    // 注销账号
    ApiResult deleteAccount(User user);

    // 修改密码
    ApiResult modifyPassword(String email, String oldPassword, String newPassword);

    // 修改用户名
    ApiResult modifyUserName(String email, String password, String newUserName);

    // 用户登录
    ApiResult userLogin(String email, String password);

    // 用户搜索
    ApiResult userSearch(Integer userId, String query);

    // 收藏商品
    ApiResult favoriteGame(Integer userId, Integer appId);

    // 取消收藏
    ApiResult unfavoriteGame(Integer userId, Integer appId);

    // 获取用户收藏列表
    ApiResult getUserFavorites(Integer userId);
}
