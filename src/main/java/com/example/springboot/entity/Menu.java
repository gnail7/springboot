package com.example.springboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 菜单/权限实体，对应表 sys_menu。
 * <p>菜单即路由配置：M(目录)/C(菜单)/F(按钮)，前端据此注册动态路由并渲染侧边栏。</p>
 */
@Data
@TableName("sys_menu")
public class Menu {

    @TableId(value = "menu_id", type = IdType.AUTO)
    private Long menuId;

    /** 菜单名称 */
    private String menuName;

    /** 父菜单ID（顶级为0） */
    private Long parentId;

    /** 显示顺序 */
    private Integer orderNum;

    /** 路由地址（相对片段，如 system/user） */
    private String path;

    /** 组件路径（前端 Vue 组件，如 system/user/index） */
    private String component;

    /** 路由参数（如 id=1&type=2） */
    private String query;

    /** 是否为外链（0是 1否） */
    private Integer isFrame;

    /** 是否缓存（0缓存 1不缓存） */
    private Integer isCache;

    /** 菜单类型（M目录 C菜单 F按钮） */
    private String menuType;

    /** 显示状态（0显示 1隐藏） */
    private String visible;

    /** 菜单状态（0正常 1停用） */
    private String status;

    /** 权限标识（如 system:user:list） */
    private String perms;

    /** 菜单图标 */
    private String icon;

    private String createBy;

    private LocalDateTime createTime;

    private String updateBy;

    private LocalDateTime updateTime;

    private String remark;

    /** 子菜单（非数据库字段） */
    @TableField(exist = false)
    private List<Menu> children;
}
