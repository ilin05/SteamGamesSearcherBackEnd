package com.steamgamessearcherbackend.service;

import com.steamgamessearcherbackend.mapper.UserMapper;
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

    private final UserMapper userMapper;

    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
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
                return ApiResult.success(email);
            }
        }catch (Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ApiResult.failure("Error judging user login");
        }
    }
}
