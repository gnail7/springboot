package com.example.springboot.service;

import com.example.springboot.entity.Menu;

import java.util.List;

/**
 * 菜单服务：菜单树、当前用户菜单树、菜单 CRUD
 */
public interface MenuService {

    /** 全量菜单树（含按钮），用于菜单管理页 / 角色授权 */
    List<Menu> listTree();

    /** 当前登录用户可见的菜单树（M/C，status=0），用于前端动态路由 */
    List<Menu> getUserMenuTree(Long userId);

    Menu getById(Long menuId);

    Long create(Menu menu);

    void update(Menu menu);

    void delete(Long menuId);
}
