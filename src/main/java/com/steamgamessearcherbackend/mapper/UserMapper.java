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

    // 获取用户Id
    @Select("select id from users where email=#{email}")
    public int getUserId(String email);

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
    @Insert("insert into games(app_id, title, released_date, win, mac, linux, price, tags, support_language, website, header_image, recommendations, positive, negative, estimated_owners, screenshots, description, movies, developers, publishers, categories, genres) values (#{appId}, #{title}, #{releasedDate}, #{win}, #{mac}, #{linux}, #{price}, #{tags}, #{supportLanguage}, #{website}, #{headerImage}, #{recommendations}, #{positive}, #{negative}, #{estimatedOwners}, #{screenshots}, #{description}, #{movies}, #{developers}, #{publishers}, #{categories}, #{genres})")
    public int storeGame(Game game);

    // 获取所有游戏
    @Select("select * from games")
    public List<Game> getAllGames();

    // 获取所有游戏，储存到ElasticGame中
    @Select("select * from games")
    public List<GameForFrontEnd> getAllElasticGames();

    // 保存搜索记录
    @Insert("insert into search_records(user_id, search_text, tags) values (#{userId}, #{query}, #{tags})")
    public int saveSearchRecord(int userId, String query, String tags);

    // 收藏商品
    @Insert("insert into favorites(user_id, app_id) values (#{userId}, #{appId})")
    public int favoriteGame(int userId, int appId);

    // 检查是否已经收藏了商品
    @Select("select count(*) from favorites where user_id=#{userId} and app_id=#{appId}")
    public int checkFavorite(int userId, int appId);

    // 取消收藏
    @Delete("delete from favorites where user_id=#{userId} and app_id=#{appId}")
    public int unfavoriteGame(int userId, int appId);

    // 获取收藏夹
    @Select("select * from games where app_id in (select app_id from favorites where user_id = #{userId})")
    public List<Game> getUserFavorites(int userId);

    // 获取搜索记录识别的tags
    @Select("select tags from search_records where user_id=#{userId}")
    public List<String> getSearchTags(int userId);

    // 根据appId获取游戏
    @Select("select * from games where app_id=#{appId}")
    public Game getGameByAppId(int appId);

    // 返回recommendations * estimated_owners 最高的前5个游戏
    @Select("select * from games order by recommendations * estimated_owners desc limit 5")
    public List<Game> getTopHotGames();
}