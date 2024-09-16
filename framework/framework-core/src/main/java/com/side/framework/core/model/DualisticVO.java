package com.side.framework.core.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

/**
 * 二元通用VO
 *
 * @author yxfl
 * @date 2023/12/03 00
 **/
@Data
@EqualsAndHashCode
public class DualisticVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 3747097285811216823L;
    /**
     * 显示值
     */
    private String label;

    /**
     * 实际值
     */
    private String value;
}
