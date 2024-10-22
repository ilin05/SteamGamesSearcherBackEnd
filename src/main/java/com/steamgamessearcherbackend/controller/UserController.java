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
}
