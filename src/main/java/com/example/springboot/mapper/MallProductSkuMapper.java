package com.example.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.springboot.mall.entity.MallProductSku;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MallProductSkuMapper extends BaseMapper<MallProductSku> {
}
