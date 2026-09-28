package com.example.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.springboot.mall.message.entity.MallMessageConsumeRecord;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MallMessageConsumeRecordMapper extends BaseMapper<MallMessageConsumeRecord> {
    @Insert("INSERT IGNORE INTO mall_message_consume_record "
            + "(consumer_group, event_id, consume_status) VALUES (#{consumerGroup}, #{eventId}, 'SUCCESS')")
    int insertIgnore(@Param("consumerGroup") String consumerGroup, @Param("eventId") String eventId);
}
