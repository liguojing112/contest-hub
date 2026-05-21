package com.example.jingsai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.jingsai.entity.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 消息Mapper
 */
@Mapper
public interface MessageMapper extends BaseMapper<Message> {

    /**
     * 根据用户ID查询消息列表
     */
    @Select("SELECT * FROM message WHERE user_id = #{userId} ORDER BY send_time DESC")
    List<Message> selectByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID和状态查询消息列表
     */
    @Select("SELECT * FROM message WHERE user_id = #{userId} AND status = #{status} ORDER BY send_time DESC")
    List<Message> selectByUserIdAndStatus(@Param("userId") Long userId, @Param("status") String status);

    /**
     * 查询未读消息数量
     */
    @Select("SELECT COUNT(*) FROM message WHERE user_id = #{userId} AND status = 'unread'")
    int countUnreadByUserId(@Param("userId") Long userId);
}