package com.example.jingsai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.jingsai.entity.Registration;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 报名Mapper
 */
@Mapper
public interface RegistrationMapper extends BaseMapper<Registration> {

    /**
     * 根据用户ID查询报名记录
     */
    @Select("SELECT * FROM registration WHERE user_id = #{userId} ORDER BY register_time DESC")
    List<Registration> selectByUserId(@Param("userId") Long userId);

    /**
     * 根据竞赛ID查询报名记录
     */
    @Select("SELECT * FROM registration WHERE competition_id = #{competitionId} ORDER BY register_time DESC")
    List<Registration> selectByCompetitionId(@Param("competitionId") Long competitionId);

    /**
     * 查询用户是否已报名某竞赛
     */
    @Select("SELECT COUNT(*) FROM registration WHERE user_id = #{userId} AND competition_id = #{competitionId}")
    int countByUserAndCompetition(@Param("userId") Long userId, @Param("competitionId") Long competitionId);
}