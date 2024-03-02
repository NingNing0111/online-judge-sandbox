package com.ningning0111.model;

import lombok.Data;

import java.util.List;

/**
 * @Project: com.ningning0111.model
 * @Author: pgthinker
 * @GitHub: https://github.com/ningning0111
 * @Date: 2024/2/29 19:09
 * @Description: 进程执行信息
 */
@Data
public class ProcessExecuteInfo {
    /**
     * 执行状态码
     */
    private Integer exitValue;
    /**
     * 执行结果信息
     */
    private List<String> executeResult;
    /**
     * 执行错误信息
     */
    private String errorMessage;
    /**
     * 执行时间
     */
    private Long time;
    /**
     * 消耗内存
     */
    private Long memory;
}

