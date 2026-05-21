package com.example.jingsai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.jingsai.entity.Review;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ReviewMapper extends BaseMapper<Review> {

    @Select("SELECT * FROM review WHERE reviewer_id = #{reviewerId} ORDER BY review_time DESC")
    List<Review> selectByReviewerId(@Param("reviewerId") Long reviewerId);

    @Select("SELECT * FROM review WHERE competition_id = #{competitionId} ORDER BY review_time DESC")
    List<Review> selectByCompetitionId(@Param("competitionId") Long competitionId);

    @Select("SELECT * FROM review WHERE registration_id = #{registrationId}")
    Review selectByRegistrationId(@Param("registrationId") Long registrationId);

    @Select("SELECT * FROM review WHERE competition_id = #{competitionId} AND status = 'pending'")
    List<Review> selectPendingByCompetitionId(@Param("competitionId") Long competitionId);
}
