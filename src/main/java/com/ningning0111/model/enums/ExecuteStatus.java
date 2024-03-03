package com.ningning0111.model.enums;

/**
 * @Project: com.ningning0111.model.enums
 * @Author: pgthinker
 * @GitHub: https://github.com/ningning0111
 * @Date: 2024/2/29 22:08
 * @Description: 程序在容器中执行的状态
 */
public enum ExecuteStatus {

    /**
     * 执行正常
     */
    EXECUTE_OK(1),
    /**
     * 程序代码错误
     */
    EXECUTE_CODE_ERROR(2),
    EXECUTE_AUTH_ERROR(4),
    /**
     * 系统错误
     */
    EXECUTE_SYSTEM_ERROR(3);

    private Integer status;
    ExecuteStatus(Integer status){
        this.status = status;
    }
    public Integer getStatus() {
        return this.status;
    }
}
