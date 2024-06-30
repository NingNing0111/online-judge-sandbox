package com.ningning0111;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Version;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import org.junit.jupiter.api.Test;

/**
 * @Project: com.ningning0111
 * @Author: pgthinker
 * @GitHub: https://github.com/ningning0111
 * @Date: 2024/3/4 22:47
 * @Description:
 */
public class DockerApiDemoTest {
    @Test
    public void test1(){
        DefaultDockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder().withDockerHost("tcp://localhost:1334").build();
        DockerClient dockerClient = DockerClientBuilder.getInstance(config).build();
        Version exec = dockerClient.versionCmd().exec();
        System.out.println(exec);
    }
}
