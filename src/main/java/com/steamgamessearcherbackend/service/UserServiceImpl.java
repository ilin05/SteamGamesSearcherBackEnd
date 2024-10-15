package com.steamgamessearcherbackend.service;

import com.steamgamessearcherbackend.service.*;
import com.steamgamessearcherbackend.entities.*;
import com.steamgamessearcherbackend.utils.ApiResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService{

    @Override
    public ApiResult openAccount(User user) {
        return null;
    }

    @Override
    public ApiResult deleteAccount(User user) {
        return null;
    }

    @Override
    public ApiResult modifyPassword(String email, String oldPassword, String newPassword) {
        return null;
    }

    @Override
    public ApiResult modifyUserName(String email, String password, String newUserName) {
        return null;
    }

    @Override
    public ApiResult userLogin(String email, String password) {
        return null;
    }
}
