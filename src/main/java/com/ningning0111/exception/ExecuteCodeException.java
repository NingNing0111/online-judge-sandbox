package com.ningning0111.exception;

import com.ningning0111.model.enums.ExecuteStatus;

/**
 * @Project: com.ningning0111.exception
 * @Author: pgthinker
 * @GitHub: https://github.com/ningning0111
 * @Date: 2024/2/29 23:46
 * @Description:
 */
public class ExecuteCodeException extends Exception{
    public ExecuteCodeException() {
       super(ExecuteStatus.EXECUTE_SYSTEM_ERROR.name() + ": A system error occurred while the code was running.");
    }
}
