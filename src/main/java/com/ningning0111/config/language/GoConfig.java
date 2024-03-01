package com.ningning0111.config.language;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @Project: com.ningning0111.config.language
 * @Author: pgthinker
 * @GitHub: https://github.com/ningning0111
 * @Date: 2024/3/1 00:56
 * @Description:
 */
@Configuration
@Data
@ConfigurationProperties(
        prefix = "sandbox.go-config"
)
public class GoConfig {
    /**
     * Go测试时的文件名
     */
    @Value("${:solution.go}")
    private String fileName;
    /**
     * Go执行时的最大限制时间
     */
    @Value("${:1000}")
    private Integer executeMaxTime;
}
