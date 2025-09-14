package com.propertysystem.Dao.Mapper;


import com.propertysystem.Controller.Pojo.Message;
import com.propertysystem.Controller.Pojo.UserVO;
import com.propertysystem.Dao.Pojo.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserMapper {

    @Select("select uuid from user where email = #{email} and password = #{password}")
    String selectByEmail(String email, String password);

    @Select("select uuid from user where username = #{username} or email = #{email}")
    String []playerExsit(String username, String email);

    @Insert("insert into user(uuid, username, password, email, role, create_time) values(#{uuid}, #{username}, #{password}, #{email}, #{role},#{create})")
    void addUser(User user);

    @Select("select uuid , username, email, role, avatar, create_time from user where uuid = #{uuid}")
    UserVO getUserInfo(String uuid);


}
