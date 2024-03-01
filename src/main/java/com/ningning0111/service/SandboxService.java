package com.ningning0111.service;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.PullImageCmd;
import com.github.dockerjava.api.command.PullImageResultCallback;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.api.model.PullResponseItem;
import com.ningning0111.model.ExecuteCodeRequest;
import com.ningning0111.model.ExecuteCodeResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

/**
 * @Project: com.ningning0111.service
 * @Author: pgthinker
 * @GitHub: https://github.com/ningning0111
 * @Date: 2024/3/1 01:30
 * @Description:
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SandboxService {

    private final SandboxFactory sandboxFactory;
    private final DockerClient dockerClient;

    @PostConstruct
    public void init() {
        String imageName = "openjdk:8-alpine";
        PullImageCmd pullImageCmd = dockerClient.pullImageCmd(imageName);
        PullImageResultCallback pullImageResultCallback = new PullImageResultCallback() {
            @Override
            public void onNext(PullResponseItem item) {
                log.info("下载镜像中:{}",item.getStatus());
                super.onNext(item);
            }
        };
        try {
            pullImageCmd
                    .exec(pullImageResultCallback)
                    .awaitCompletion();
        }catch (InterruptedException e){
            throw new RuntimeException(e);
        }
    }

    public ExecuteCodeResponse execute(ExecuteCodeRequest request){
        String language = request.getLanguage();
        CodeSandbox sandbox = sandboxFactory.createSandbox(language);
        ExecuteCodeResponse response = sandbox.executeCode(request);
        return response;
    }
}
