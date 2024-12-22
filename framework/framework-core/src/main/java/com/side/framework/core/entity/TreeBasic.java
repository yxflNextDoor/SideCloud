package com.side.framework.core.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 树型组织结构基类
 *
 * @author yxfl
 * @date 2023/12/21 22
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TreeBasic<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = 2402426107666574729L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 父id
     */
    private Long parentId;

    /**
     * 左值
     */
    private Integer leftIndex;

    /**
     * 右值
     */
    private Integer rightIndex;

    /**
     * 层级
     */
    private Integer level;

    /**
     * 排序分数
     */
    private Integer sort;

    /**
     * 数据
     */
    private T data;

    private List<TreeBasic<? extends T>> childrenList;

}
