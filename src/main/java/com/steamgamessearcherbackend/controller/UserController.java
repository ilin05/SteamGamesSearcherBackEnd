package com.steamgamessearcherbackend.controller;

import com.steamgamessearcherbackend.entities.*;
import com.steamgamessearcherbackend.service.UserService;
import com.steamgamessearcherbackend.utils.ApiResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Map.Entry;

@CrossOrigin(methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    // 开户
    @PostMapping("/openAccount")
    public ApiResult openAccount(@RequestBody Map<String,String> newAccountInfo) {
        User user = new User();
        user.setPassword(newAccountInfo.get("password"));
        user.setEmail(newAccountInfo.get("email"));
        user.setUserName(newAccountInfo.get("userName"));
        return userService.openAccount(user);
    }

    // 销户
    @PostMapping("/deleteAccount")
    public ApiResult deleteAccount(@RequestBody User user) {
        return userService.deleteAccount(user);
    }

    // 修改密码
    @PostMapping("/modifyPassword")
    public ApiResult modifyPassword(@RequestBody Map<String,String> modifytPasswordInfo) {
        System.out.println(modifytPasswordInfo);
        String email = modifytPasswordInfo.get("email");
        String oldPassword = modifytPasswordInfo.get("oldPassword");
        String newPassword = modifytPasswordInfo.get("newPassword");
        return userService.modifyPassword(email, oldPassword, newPassword);
    }

    // 修改用户名
    @PostMapping("/modifyUserName")
    public ApiResult modifyUserName(@RequestBody Map<String,String> modifytUserName) {
        String email = modifytUserName.get("email");
        String password = modifytUserName.get("password");
        String newName = modifytUserName.get("newName");
        return userService.modifyUserName(email, password, newName);
    }

    // 用户登录
    @PostMapping("/login")
    public ApiResult userLogin(@RequestBody Map<String,String> userLoginInfo) {
        String email = userLoginInfo.get("email");
        String password = userLoginInfo.get("password");
        System.out.println(email);
        System.out.println(password);
        return userService.userLogin(email, password);
    }

    // 获取推荐游戏
    @GetMapping("/recommendGames")
    public ApiResult recommendGames(@RequestParam Integer userId) throws IOException {
        return userService.recommendGames(userId);
    }

    // 收藏游戏
    @PostMapping("/addFavorites")
    public ApiResult addFavorites(@RequestBody Map<String,Integer> addFavoritesInfo) {
        Integer userId = addFavoritesInfo.get("userId");
        Integer appId = addFavoritesInfo.get("appId");
        System.out.println(userId);
        System.out.println(appId);
        return userService.favoriteGame(userId, appId);
    }

    // 取消收藏
    @PostMapping("/deleteFavorites")
    public ApiResult deleteFavorites(@RequestBody Map<String,Integer> deleteFavoritesInfo) {
        Integer userId = deleteFavoritesInfo.get("userId");
        Integer appId = deleteFavoritesInfo.get("appId");
        return userService.unfavoriteGame(userId, appId);
    }

    // 获取用户收藏列表
    @GetMapping("/getFavorites")
    public ApiResult getUserFavorites(@RequestParam Integer userId) {
        return userService.getUserFavorites(userId);
    }

    // 用户搜索
    @GetMapping("/search")
    public ApiResult userSearch(@RequestParam Integer userId, @RequestParam String query, @RequestParam List<String> tags, @RequestParam List<String> supportLanguages, @RequestParam Double lowestPrice, @RequestParam Double highestPrice) {
//        Integer userId = Integer.parseInt(searchInfo.get("userId").toString());
//        String query = searchInfo.get("query").toString();
//        List<String> tags = (List<String>) searchInfo.get("tags");
//        List<String> supportLanguages = (List<String>) searchInfo.get("supportLanguages");
        System.out.println("userId: " + userId);
        System.out.println("query: " + query);
        System.out.println("tags: " + tags);
        System.out.println("supportLanguages: " + supportLanguages);
        System.out.println("lowestPrice: " + lowestPrice);
        System.out.println("highestPrice: " + highestPrice);
        return userService.userSearch(userId, query);
    }
}
