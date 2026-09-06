package com.example.springboot.controller;

import com.example.springboot.entity.Menu;
import com.example.springboot.service.MenuService;
import com.example.springboot.utils.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 菜单管理接口：菜单树、当前用户菜单树、菜单 CRUD
 */
@Tag(name = "菜单管理")
@RestController
@RequestMapping("/api/menu")
public class MenuController {

    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    @Operation(summary = "全量菜单树（含按钮，菜单管理/角色授权用）")
    @GetMapping("/list")
    public Result<List<Menu>> list() {
        return Result.success(menuService.listTree());
    }

    @Operation(summary = "当前登录用户的菜单树（M/C，用于注册前端动态路由）")
    @GetMapping("/userMenus")
    public Result<List<Menu>> userMenus(@RequestAttribute("userId") Long userId) {
        return Result.success(menuService.getUserMenuTree(userId));
    }

    @Operation(summary = "菜单详情")
    @GetMapping("/{menuId}")
    public Result<Menu> get(@PathVariable Long menuId) {
        return Result.success(menuService.getById(menuId));
    }

    @Operation(summary = "新增菜单")
    @PostMapping
    public Result<Long> create(@RequestBody Menu menu) {
        return Result.success(menuService.create(menu));
    }

    @Operation(summary = "修改菜单")
    @PutMapping("/{menuId}")
    public Result<Void> update(@PathVariable Long menuId, @RequestBody Menu menu) {
        menu.setMenuId(menuId);
        menuService.update(menu);
        return Result.success();
    }

    @Operation(summary = "删除菜单（清理角色-菜单关联；有子菜单时拒绝）")
    @DeleteMapping("/{menuId}")
    public Result<Void> delete(@PathVariable Long menuId) {
        menuService.delete(menuId);
        return Result.success();
    }
}
