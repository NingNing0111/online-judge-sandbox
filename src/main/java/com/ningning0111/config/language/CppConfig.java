package com.ningning0111.config.language;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @Project: com.ningning0111.config.language
 * @Author: pgthinker
 * @GitHub: https://github.com/ningning0111
 * @Date: 2024/3/1 00:55
 * @Description:
 */
@Configuration
@Data
@ConfigurationProperties(
        prefix = "sandbox.cpp-config"
)
public class CppConfig {
    /**
     * C++测试时的文件名
     */
    @Value("${:solution.cpp}")
    private String fileName;
    /**
     * C++执行的最大限制时间
     */
    @Value("${:1000}")
    private Integer executeMaxTime;
}
