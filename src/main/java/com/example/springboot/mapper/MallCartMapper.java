package com.example.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.springboot.mall.entity.MallCart;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface MallCartMapper extends BaseMapper<MallCart> {
    @Select("SELECT * FROM mall_cart WHERE member_id = #{memberId} AND status = 'ACTIVE' LIMIT 1")
    MallCart findActiveByMemberId(@Param("memberId") Long memberId);
}
