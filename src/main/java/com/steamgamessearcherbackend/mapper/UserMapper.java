package com.steamgamessearcherbackend.mapper;

import com.steamgamessearcherbackend.entities.*;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface UserMapper {

    // 创建用户
    @Insert("INSERT INTO users(username, password, email) values (#{userName}, #{password}, #{email})")
    public int openAccount(String userName, String password, String email);
    //public int openAccount(User user);

    // 根据邮箱获取用户信息
    @Select("select * from users where email = #{email}")
    public User getUserByEmail(String email);

    // 检查用户名唯一性
    @Select("select count(*) from users where username=#{name}")
    public int checkUserName(String name);

    // 检查邮箱唯一性
    @Select("select count(*) from users where email=#{email}")
    public int checkEmail(String email);

    // 匹配账户密码
    @Select("select count(*) from users where email=#{email} and password=#{password}")
    public int judgePassword(String email, String password);

    // 修改密码
    @Update("update users set password=#{newPassword} where email=#{email}")
    public int updatePassword(String newPassword, String email);

    // 修改用户名
    @Update("update users set username=#{newName} where email=#{email}")
    public int updateUserName(String email, String newName);

    // 注销账号
    @Delete("delete from users where email=#{email}")
    public int deleteUser(String email);

    // 存储游戏数据
    @Insert("insert into games(app_id, title, released_date, win, mac, linux, price, tags, support_language, website, header_image, recommendations, positive, negative, estimated_owners, screenshots, description) values (#{appId}, #{title}, #{releaseDate}, #{winSupport}, #{macSupport}, #{linuxSupport}, #{price}, #{tags}, #{supportLanguage}, #{website}, #{headerImage}, #{recommendations}, #{positive}, #{negative}, #{estimatedOwners}, #{screenshots}, #{description})")
    public int storeGame(Game game);
}
