package com.ningning0111.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Project: com.ningning0111.model
 * @Author: pgthinker
 * @GitHub: https://github.com/ningning0111
 * @Date: 2024/3/2 15:10
 * @Description: 指令执行的信息
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CmdExecuteInfo {
    private String message;
    private String errMessage;
    private Long time;
    private Long memory;
}
