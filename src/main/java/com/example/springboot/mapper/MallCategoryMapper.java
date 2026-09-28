package com.example.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.springboot.mall.entity.MallCategory;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MallCategoryMapper extends BaseMapper<MallCategory> {
}
