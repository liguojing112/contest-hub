package com.example.jingsai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.jingsai.entity.Team;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TeamMapper extends BaseMapper<Team> {

    @Select("SELECT * FROM team WHERE leader_id = #{leaderId} ORDER BY create_time DESC")
    List<Team> selectByLeaderId(@Param("leaderId") Long leaderId);

    @Select("SELECT * FROM team WHERE competition_id = #{competitionId} ORDER BY create_time DESC")
    List<Team> selectByCompetitionId(@Param("competitionId") Long competitionId);

    @Select("SELECT * FROM team WHERE team_code = #{teamCode}")
    Team selectByTeamCode(@Param("teamCode") String teamCode);

    @Select("SELECT * FROM team WHERE advisor_id = #{advisorId} ORDER BY create_time DESC")
    List<Team> selectByAdvisorId(@Param("advisorId") Long advisorId);

    @Select("<script>SELECT * FROM team WHERE competition_id IN <foreach collection='competitionIds' item='id' open='(' separator=',' close=')'>#{id}</foreach> ORDER BY create_time DESC</script>")
    List<Team> selectByCompetitionIds(@Param("competitionIds") List<Long> competitionIds);
}
