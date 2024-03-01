package com.ningning0111.service.impl;

import cn.hutool.core.util.StrUtil;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.api.model.*;
import com.ningning0111.config.SandboxFullConfig;
import com.ningning0111.exception.ExecuteCodeException;
import com.ningning0111.model.ProcessExecuteInfo;
import com.ningning0111.model.enums.ExecuteStatus;
import com.ningning0111.service.AbstractCodeSandbox;
import com.ningning0111.utils.DockerUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * @Project: com.ningning0111.service
 * @Author: pgthinker
 * @GitHub: https://github.com/ningning0111
 * @Date: 2024/2/29 21:42
 * @Description:
 */
@RequiredArgsConstructor
@Component("javaCodeSandbox")
@Slf4j
public class JavaCodeSandbox extends AbstractCodeSandbox {

    private final SandboxFullConfig sandboxFullConfig;
    private final DockerClient dockerClient;

    @Override
    public String getCodeDirPath(String workDir) {
        return workDir + File.separator + sandboxFullConfig.getCodeDirName();
    }

    @Override
    public String getCodeFilePath(String parentPath) {
        return parentPath + File.separator + sandboxFullConfig.getJavaConfig().getFileName();
    }

    @Override
    public ProcessExecuteInfo execute(File file) throws ExecuteCodeException {
        String containerId = null;
        try{
            // 1. 先编译成子节码
            boolean compile = compile(file);
            if(!compile){
                throw new ExecuteCodeException();
            }
            // 2. 再放到容器中执行
            // 2.1 创建容器
            containerId = initContainer(file);
            // 2.2 启动容器
            dockerClient.startContainerCmd(containerId).exec();
            // 2.3 执行命令
            return executeCmd(containerId);
        }catch (Exception e){
            throw new ExecuteCodeException();
        }finally {
            if(StrUtil.isNotBlank(containerId)){
                dockerClient.stopContainerCmd(containerId).exec();
                dockerClient.removeContainerCmd(containerId).exec();
            }
        }
    }



    /**
     * 编译
     * @param codeFile
     * @return
     */
    private boolean compile(File codeFile){
        String compileCmd = String.format("javac -encoding utf-8 %s", codeFile.getAbsolutePath());
        try {
            Process compileProcess = Runtime.getRuntime().exec(compileCmd);
            int i = compileProcess.waitFor();
            if(i != 0){
                return false;
            }
        } catch (Exception e){
            return false;
        }
        return true;
    }

    /**
     * 初始化容器
     * @param codeFile
     * @return
     */
    private String initContainer(File codeFile){
        String codeFileAbsolutePath = codeFile.getParentFile().getAbsolutePath();
        CreateContainerCmd containerCmd = dockerClient.createContainerCmd(
                sandboxFullConfig.getSandboxConfig().getImageName()
        );
        HostConfig hostConfig = new HostConfig();
        hostConfig.withMemory(sandboxFullConfig.getSandboxConfig().getMaxMemory());
        hostConfig.withCpuCount(sandboxFullConfig.getSandboxConfig().getCpuCount());
        hostConfig.setBinds(new Bind(codeFileAbsolutePath, new Volume("/app")));

        CreateContainerResponse createContainerResponse = containerCmd
                .withHostConfig(hostConfig)
                .withNetworkDisabled(true)
                .withReadonlyRootfs(true)
                .withAttachStdin(true)
                .withAttachStderr(true)
                .withAttachStdout(true)
                .withTty(true)
                .exec();
        return createContainerResponse.getId();
    }

    /**
     * docker exec -it c9f /bin/sh -c "java -cp /app Solution < /app/input.in"
     * @param containerId
     * @return
     */
    private ProcessExecuteInfo executeCmd(String containerId) {
        String fileName = sandboxFullConfig.getJavaConfig().getFileName();
        int endIndex = fileName.lastIndexOf(".java");
        String cmdFileName = fileName.substring(0, endIndex);
        String[] args = new String[]{"sh","-c","java -cp /app " + cmdFileName + " < /app/input.in"};
        // 创建指令执行对象
        ExecCreateCmdResponse execCmdResponse = DockerUtils.createCmdResponse(dockerClient, args, containerId);
        // 获取Docker的执行进程信息
        ProcessExecuteInfo processExecInfo = DockerUtils.getProcessExecInfo(
                execCmdResponse,
                dockerClient,
                containerId,
                sandboxFullConfig.getJavaConfig().getExecuteMaxTime(),
                TimeUnit.MILLISECONDS
        );
//        log.error("获取到的进程信息:{}",processExecInfo);

        if(processExecInfo.getErrorMessage() != null){
            processExecInfo.setExitValue(ExecuteStatus.EXECUTE_SYSTEM_ERROR.getStatus());
            return processExecInfo;
        }
        // 获取消耗的时间
        Long time = processExecInfo.getTime();
        if(time >= sandboxFullConfig.getJavaConfig().getExecuteMaxTime()){
            processExecInfo.setExitValue(ExecuteStatus.EXECUTE_TIMEOUT.getStatus());
            processExecInfo.setMessage(ExecuteStatus.EXECUTE_TIMEOUT.name());
            return processExecInfo;
        }
        // 获取内存大小
        Long memory = processExecInfo.getMemory();
        log.error("最大内存大小：{}",memory);
        if(memory >= sandboxFullConfig.getSandboxConfig().getMaxMemory() - 1048576L){
            processExecInfo.setExitValue(ExecuteStatus.EXECUTE_MEMORY_LIMIT_EXCEEDED.getStatus());
            processExecInfo.setMessage(ExecuteStatus.EXECUTE_MEMORY_LIMIT_EXCEEDED.name());
            return processExecInfo;
        }
        processExecInfo.setExitValue(ExecuteStatus.EXECUTE_PASS.getStatus());
        return processExecInfo;
    }

}
