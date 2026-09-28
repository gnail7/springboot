package com.example.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.springboot.mall.entity.MallOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface MallOrderMapper extends BaseMapper<MallOrder> {
    @Select("SELECT * FROM mall_order WHERE member_id = #{memberId} ORDER BY id DESC LIMIT #{offset}, #{limit}")
    List<MallOrder> findPageByMemberId(@Param("memberId") Long memberId, @Param("offset") long offset, @Param("limit") long limit);

    @Select("SELECT * FROM mall_order WHERE member_id = #{memberId} AND idempotency_key = #{idempotencyKey} LIMIT 1")
    MallOrder findByIdempotencyKey(@Param("memberId") Long memberId, @Param("idempotencyKey") String idempotencyKey);

    @Select("SELECT * FROM mall_order WHERE order_no = #{orderNo} AND member_id = #{memberId} LIMIT 1")
    MallOrder findByOrderNo(@Param("memberId") Long memberId, @Param("orderNo") String orderNo);

    @Update("UPDATE mall_order SET order_status = #{toStatus}, version = version + 1 "
            + "WHERE id = #{id} AND order_status = #{fromStatus} AND version = #{version}")
    int changeStatus(@Param("id") Long id, @Param("fromStatus") String fromStatus, @Param("toStatus") String toStatus, @Param("version") Integer version);

    @Select("SELECT * FROM mall_order WHERE order_status = 'PENDING_PAYMENT' AND expire_at < #{now} ORDER BY id LIMIT #{limit}")
    List<MallOrder> findExpired(@Param("now") LocalDateTime now, @Param("limit") int limit);
}
