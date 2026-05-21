package com.example.jingsai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.jingsai.entity.Appeal;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AppealMapper extends BaseMapper<Appeal> {

    @Select("SELECT * FROM appeal WHERE student_id = #{studentId} ORDER BY create_time DESC")
    List<Appeal> selectByStudentId(@Param("studentId") Long studentId);

    @Select("SELECT * FROM appeal WHERE status = #{status} ORDER BY create_time DESC")
    List<Appeal> selectByStatus(@Param("status") String status);
}
