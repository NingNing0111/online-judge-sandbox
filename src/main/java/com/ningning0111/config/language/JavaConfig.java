package com.ningning0111.config.language;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @Project: com.ningning0111.config.language
 * @Author: pgthinker
 * @GitHub: https://github.com/ningning0111
 * @Date: 2024/3/1 00:47
 * @Description:
 */
@Data
@Configuration
@ConfigurationProperties(
        prefix = "sandbox.java-config"
)
public class JavaConfig {
    /**
     * Java测试时的文件名
     */
    @Value("${:Solution.java}")
    private String fileName;
    /**
     * Java执行时的最大限制时间
     */
    @Value("${:2000}")
    private Integer executeMaxTime;
}
