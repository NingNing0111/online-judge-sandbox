package com.ningning0111.config;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
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
     * 暂存代码的文件名
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
     * 运行时的最大等待时间 这里设置为3s
     */
    @Value("${:3000}")
    private long maxTime;
    /**
     * CPU数
     */
    @Value("${:1}")
    private Long cpuCount;
    /**
     * 语言文件名配置
     */
    @Value("${:Solution.java}")
    private String javaFileName;
    @Value("${:solution.java}")
    private String cppFileName;
    @Value("${:solution.go}")
    private String goFileName;


    private String dockerHost;

    /**
     * 沙盒验证密钥
     */
    private String secretKey;
    @Bean
    public DockerClient dockerClient(){
        System.err.println(dockerHost);
        DefaultDockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost(dockerHost)
                .build();
        return DockerClientBuilder.getInstance(config).build();
    }
}
