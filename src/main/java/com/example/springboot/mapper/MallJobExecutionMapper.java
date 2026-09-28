package com.example.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.springboot.mall.job.entity.MallJobExecution;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MallJobExecutionMapper extends BaseMapper<MallJobExecution> {
}
