package com.example.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.springboot.mall.entity.MallProductSpu;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MallProductSpuMapper extends BaseMapper<MallProductSpu> {
}
