package com.example.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.springboot.mall.entity.MallOrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MallOrderItemMapper extends BaseMapper<MallOrderItem> {
    @Select("SELECT * FROM mall_order_item WHERE order_id = #{orderId} ORDER BY id")
    List<MallOrderItem> findByOrderId(@Param("orderId") Long orderId);
}
