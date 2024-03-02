package com.ningning0111.model;

import lombok.Builder;
import lombok.Data;

/**
 * @Project: com.ningning0111.model
 * @Author: pgthinker
 * @GitHub: https://github.com/ningning0111
 * @Date: 2024/2/29 19:05
 * @Description:
 */
@Data
@Builder
public class JudgeInfo {

    /**
     * 消耗内存
     */
    private Long memory;

    /**
     * 消耗时间（KB）
     */
    private Long time;

}
