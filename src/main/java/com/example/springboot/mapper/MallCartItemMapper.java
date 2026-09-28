package com.example.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.springboot.mall.entity.MallCartItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MallCartItemMapper extends BaseMapper<MallCartItem> {
    @Select("SELECT * FROM mall_cart_item WHERE member_id = #{memberId} ORDER BY id DESC")
    List<MallCartItem> findByMemberId(@Param("memberId") Long memberId);

    @Select("SELECT * FROM mall_cart_item WHERE member_id = #{memberId} AND sku_id = #{skuId} LIMIT 1")
    MallCartItem findByMemberAndSku(@Param("memberId") Long memberId, @Param("skuId") Long skuId);
}
