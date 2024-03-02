package com.ningning0111.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Project: com.ningning0111.model
 * @Author: pgthinker
 * @GitHub: https://github.com/ningning0111
 * @Date: 2024/2/29 19:05
 * @Description:
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExecuteCodeRequest {

    /**
     * 输入数据
     */
    private List<String> input;
    /**
     * 代码
      */
    private String code;
    /**
     * 语言
     */
    private String language;

}
