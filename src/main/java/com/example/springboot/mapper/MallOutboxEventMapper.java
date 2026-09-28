package com.example.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.springboot.mall.message.entity.MallOutboxEvent;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface MallOutboxEventMapper extends BaseMapper<MallOutboxEvent> {
    @Select("SELECT * FROM mall_outbox_event WHERE publish_status = 'PENDING' "
            + "AND (next_retry_at IS NULL OR next_retry_at <= #{now}) ORDER BY id LIMIT #{limit}")
    List<MallOutboxEvent> findPending(@Param("now") LocalDateTime now, @Param("limit") int limit);

    @Update("UPDATE mall_outbox_event SET publish_status = 'PUBLISHED', published_at = NOW(), "
            + "updated_at = NOW() WHERE id = #{id} AND publish_status = 'PENDING'")
    int markPublished(@Param("id") Long id);

    @Update("UPDATE mall_outbox_event SET retry_count = retry_count + 1, next_retry_at = #{nextRetryAt}, "
            + "last_error = #{error}, updated_at = NOW() WHERE id = #{id}")
    int markRetry(@Param("id") Long id, @Param("nextRetryAt") LocalDateTime nextRetryAt,
                  @Param("error") String error);
}
