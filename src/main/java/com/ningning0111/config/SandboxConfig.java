package com.ningning0111.config;

import com.ningning0111.config.language.CppConfig;
import com.ningning0111.config.language.GoConfig;
import com.ningning0111.config.language.JavaConfig;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @Project: com.ningning0111.config
 * @Author: pgthinker
 * @GitHub: https://github.com/ningning0111
 * @Date: 2024/3/1 00:24
 * @Description:
 */
@Configuration
@ConfigurationProperties(prefix = "sandbox")
@Data
public class SandboxConfig {

    /**
     * 代码存储的文件名
     */
    @Value("${:tempCode}")
    private String codeDirName;
    /**
     * 沙盒运行环境镜像名
     */
    @Value("${:openjdk:8-alpine}")
    private String imageName;
    /**
     * 运行时的最大内存限制
     */
    @Value("${:104857600}")
    private long maxMemory;
    /**
     * CPU数
     */
    @Value("${:1}")
    private Long cpuCount;

}
