package com.example.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.springboot.mall.entity.MallInventory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface MallInventoryMapper extends BaseMapper<MallInventory> {
    @Update("UPDATE mall_inventory SET available_stock = available_stock - #{quantity}, "
            + "locked_stock = locked_stock + #{quantity}, version = version + 1 "
            + "WHERE sku_id = #{skuId} AND available_stock >= #{quantity} AND version = #{version}")
    int reserve(@Param("skuId") Long skuId, @Param("quantity") Integer quantity, @Param("version") Integer version);

    @Update("UPDATE mall_inventory SET available_stock = available_stock + #{quantity}, "
            + "locked_stock = locked_stock - #{quantity}, version = version + 1 "
            + "WHERE sku_id = #{skuId} AND locked_stock >= #{quantity} AND version = #{version}")
    int release(@Param("skuId") Long skuId, @Param("quantity") Integer quantity, @Param("version") Integer version);
}
