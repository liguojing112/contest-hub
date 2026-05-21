package com.example.jingsai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.jingsai.entity.Competition;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 竞赛Mapper
 */
@Mapper
public interface CompetitionMapper extends BaseMapper<Competition> {

    /**
     * 根据创建者ID查询竞赛列表
     */
    @Select("SELECT * FROM competition WHERE creator_id = #{creatorId} ORDER BY create_time DESC")
    List<Competition> selectByCreatorId(@Param("creatorId") Long creatorId);

    /**
     * 根据状态查询竞赛列表
     */
    @Select("SELECT * FROM competition WHERE status = #{status} ORDER BY create_time DESC")
    List<Competition> selectByStatus(@Param("status") String status);

    /**
     * 查询活跃竞赛(进行中的竞赛)
     */
    @Select("SELECT * FROM competition WHERE status IN ('active', 'enrolling') ORDER BY start_time DESC")
    List<Competition> selectActiveCompetitions();
}