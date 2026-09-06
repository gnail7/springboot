package com.example.springboot.service.impl;

import com.example.springboot.entity.Menu;
import com.example.springboot.exception.BusinessException;
import com.example.springboot.mapper.MenuMapper;
import com.example.springboot.service.MenuService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 菜单服务实现：构建菜单树、当前用户菜单树、菜单 CRUD
 */
@Service
public class MenuServiceImpl implements MenuService {

    private final MenuMapper menuMapper;

    public MenuServiceImpl(MenuMapper menuMapper) {
        this.menuMapper = menuMapper;
    }

    @Override
    public List<Menu> listTree() {
        return buildTree(menuMapper.selectMenuList(), 0L);
    }

    @Override
    public List<Menu> getUserMenuTree(Long userId) {
        return buildTree(menuMapper.selectMenusByUserId(userId), 0L);
    }

    @Override
    public Menu getById(Long menuId) {
        Menu menu = menuMapper.selectById(menuId);
        if (menu == null) {
            throw new BusinessException(404, "菜单不存在");
        }
        return menu;
    }

    @Override
    public Long create(Menu menu) {
        if (!StringUtils.hasText(menu.getMenuName())) {
            throw new BusinessException(400, "菜单名称不能为空");
        }
        fillDefault(menu);
        menu.setCreateTime(LocalDateTime.now());
        menuMapper.insert(menu);
        return menu.getMenuId();
    }

    @Override
    public void update(Menu menu) {
        if (menu.getMenuId() == null) {
            throw new BusinessException(400, "缺少菜单ID");
        }
        getById(menu.getMenuId());
        if (Objects.equals(menu.getMenuId(), menu.getParentId())) {
            throw new BusinessException(400, "父菜单不能设置为自身");
        }
        if (!StringUtils.hasText(menu.getMenuName())) {
            throw new BusinessException(400, "菜单名称不能为空");
        }
        menu.setUpdateTime(LocalDateTime.now());
        menuMapper.updateById(menu);
    }

    @Override
    public void delete(Long menuId) {
        getById(menuId);
        Long children = menuMapper.countChildren(menuId);
        if (children != null && children > 0) {
            throw new BusinessException(400, "存在子菜单，无法删除");
        }
        menuMapper.deleteById(menuId);
        menuMapper.deleteRoleMenuByMenuId(menuId);
    }

    /** 补全默认字段 */
    private void fillDefault(Menu menu) {
        if (menu.getParentId() == null) {
            menu.setParentId(0L);
        }
        if (menu.getOrderNum() == null) {
            menu.setOrderNum(0);
        }
        if (!StringUtils.hasText(menu.getMenuType())) {
            menu.setMenuType("C");
        }
        if (!StringUtils.hasText(menu.getVisible())) {
            menu.setVisible("0");
        }
        if (!StringUtils.hasText(menu.getStatus())) {
            menu.setStatus("0");
        }
        if (menu.getIsFrame() == null) {
            menu.setIsFrame(1);
        }
        if (menu.getIsCache() == null) {
            menu.setIsCache(0);
        }
    }

    /** 把扁平列表构建成菜单树（parentId 匹配 + order_num 排序） */
    private List<Menu> buildTree(List<Menu> all, Long parentId) {
        return all.stream()
                .filter(menu -> Objects.equals(parentId, menu.getParentId()))
                .sorted(Comparator
                        .comparing(Menu::getOrderNum, Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(Menu::getMenuId, Comparator.nullsLast(Comparator.naturalOrder())))
                .map(menu -> {
                    menu.setChildren(buildTree(all, menu.getMenuId()));
                    return menu;
                })
                .collect(Collectors.toList());
    }
}
