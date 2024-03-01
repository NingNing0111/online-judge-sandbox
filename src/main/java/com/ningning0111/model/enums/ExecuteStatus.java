package com.ningning0111.model.enums;

/**
 * @Project: com.ningning0111.model.enums
 * @Author: pgthinker
 * @GitHub: https://github.com/ningning0111
 * @Date: 2024/2/29 22:08
 * @Description:
 */
public enum ExecuteStatus {
    /**
     * 运行正常
     */
    EXECUTE_PASS(1),
    /**
     * 运行过程中系统错误
     */
    EXECUTE_SYSTEM_ERROR(2),
    /**
     * 超时
     */
    EXECUTE_TIMEOUT(4),
    /**
     * 内存溢出
     */
    EXECUTE_MEMORY_LIMIT_EXCEEDED(5),
    /**
     * 运行错误
     */
    EXECUTE_ERROR(3);
    private Integer status;
    ExecuteStatus(Integer status){
        this.status = status;
    }
    public Integer getStatus() {
        return this.status;
    }
}
