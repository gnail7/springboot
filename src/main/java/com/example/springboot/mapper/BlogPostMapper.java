package com.example.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.springboot.entity.BlogPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 博客文章 Mapper（分页联表 + 文章-标签关联写在 XML）
 */
@Mapper
public interface BlogPostMapper extends BaseMapper<BlogPost> {

    List<BlogPost> page(@Param("title") String title,
                        @Param("categoryId") Long categoryId,
                        @Param("status") String status,
                        @Param("offset") int offset,
                        @Param("pageSize") int pageSize);

    Long count(@Param("title") String title,
               @Param("categoryId") Long categoryId,
               @Param("status") String status);

    /** 文章详情（带上分类名称） */
    BlogPost selectDetail(@Param("postId") Long postId);

    /** 某文章已关联的标签 id */
    List<Long> selectTagIdsByPostId(@Param("postId") Long postId);

    /** 覆盖式保存文章-标签 */
    int insertPostTag(@Param("postId") Long postId, @Param("tagIds") List<Long> tagIds);

    int deletePostTagByPostId(@Param("postId") Long postId);

    /** 某一标签被多少篇文章引用（删除前校验） */
    Long countPostByTagId(@Param("tagId") Long tagId);

    /* ---------- 公开只读（仅已发布） ---------- */

    List<BlogPost> selectPublicPage(@Param("categoryId") Long categoryId,
                                    @Param("tagId") Long tagId,
                                    @Param("keyword") String keyword,
                                    @Param("offset") int offset,
                                    @Param("pageSize") int pageSize);

    Long countPublic(@Param("categoryId") Long categoryId,
                     @Param("tagId") Long tagId,
                     @Param("keyword") String keyword);

    /** 公开详情：仅已发布 */
    BlogPost selectPublicDetail(@Param("postId") Long postId);
}
