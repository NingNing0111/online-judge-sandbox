package com.ningning0111.config;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import com.ningning0111.config.language.CppConfig;
import com.ningning0111.config.language.GoConfig;
import com.ningning0111.config.language.JavaConfig;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @Project: com.ningning0111.config
 * @Author: pgthinker
 * @GitHub: https://github.com/ningning0111
 * @Date: 2024/3/1 01:08
 * @Description:
 */
@Component
@RequiredArgsConstructor
@ToString
public class SandboxFullConfig {
    @Getter
    private final SandboxConfig sandboxConfig;
    @Getter
    private final JavaConfig javaConfig;
    @Getter
    private final GoConfig goConfig;
    @Getter
    private final CppConfig cppConfig;
    public String getCodeDirName(){
        return this.sandboxConfig.getCodeDirName();
    }

    @Bean
    public DockerClient dockerClient() {
        return DockerClientBuilder.getInstance().build();
    }

}
