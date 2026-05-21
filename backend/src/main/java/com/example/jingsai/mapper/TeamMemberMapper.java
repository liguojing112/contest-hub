package com.example.jingsai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.jingsai.entity.TeamMember;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TeamMemberMapper extends BaseMapper<TeamMember> {

    @Select("SELECT * FROM team_member WHERE team_id = #{teamId}")
    List<TeamMember> selectByTeamId(@Param("teamId") Long teamId);

    @Select("SELECT * FROM team_member WHERE user_id = #{userId}")
    List<TeamMember> selectByUserId(@Param("userId") Long userId);

    @Select("SELECT COUNT(*) FROM team_member WHERE team_id = #{teamId} AND user_id = #{userId}")
    int countByTeamAndUser(@Param("teamId") Long teamId, @Param("userId") Long userId);

    @Select("SELECT * FROM team_member WHERE user_id = #{userId} AND status = 'pending'")
    List<TeamMember> selectPendingByUserId(@Param("userId") Long userId);
}
