package com.github.liueyueyi.forum.api.model.vo.article;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 保存Column文章请求参数
 *
 * @author LouZai
 * @date 2022/9/26
 */
@Data
public class ColumnArticleReq implements Serializable {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 专栏ID
     */
    private Long columnId;

    /**
     * 文章ID
     */
    private Long articleId;

    /**
     * 顺序，越小越靠前
     */
    private Integer section;
}
