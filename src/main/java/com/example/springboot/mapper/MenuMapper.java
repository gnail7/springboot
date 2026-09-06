package com.example.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.springboot.entity.Menu;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 菜单 Mapper
 * <p>基础 CRUD 由 MyBatis-Plus BaseMapper 生成；复杂树查询写在 XML 里。</p>
 */
@Mapper
public interface MenuMapper extends BaseMapper<Menu> {

    /** 全部菜单（含按钮），用于菜单管理树 / 角色授权树 */
    List<Menu> selectMenuList();

    /** 用户的可见菜单（M/C，status=0），用于注册前端动态路由 */
    List<Menu> selectMenusByUserId(Long userId);

    /** 统计某菜单下是否有子菜单（删除前置校验） */
    Long countChildren(Long parentId);

    /** 删除某菜单时清理角色-菜单关联 */
    int deleteRoleMenuByMenuId(Long menuId);
}
