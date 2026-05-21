package com.example.jingsai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.jingsai.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户Mapper
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据用户名查询用户
     */
    @Select("SELECT * FROM user WHERE username = #{username}")
    User selectByUsername(@Param("username") String username);

    /**
     * 根据角色查询用户列表
     */
    @Select("SELECT * FROM user WHERE role = #{role}")
    List<User> selectByRole(@Param("role") String role);

    /**
     * 检查用户名是否存在
     */
    @Select("SELECT COUNT(*) FROM user WHERE username = #{username}")
    int countByUsername(@Param("username") String username);

    @Select("SELECT COUNT(*) FROM user WHERE phone = #{phone}")
    int countByPhone(@Param("phone") String phone);

    @Select("SELECT COUNT(*) FROM user WHERE email = #{email}")
    int countByEmail(@Param("email") String email);
}