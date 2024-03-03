package com.ningning0111.service.impl;

import cn.hutool.core.util.StrUtil;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.api.model.*;
import com.ningning0111.config.SandboxConfig;
import com.ningning0111.exception.ExecuteCodeException;
import com.ningning0111.model.CmdExecuteInfo;
import com.ningning0111.model.ProcessExecuteInfo;
import com.ningning0111.model.enums.ExecuteStatus;
import com.ningning0111.service.AbstractCodeSandbox;
import com.ningning0111.utils.DockerUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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

    private final DockerClient dockerClient;
    private final SandboxConfig sandboxConfig;

    @Override
    public String getCodeDirPath(String workDir) {
        return workDir + File.separator + sandboxConfig.getCodeDirName();
    }

    @Override
    public String getCodeFilePath(String parentPath) {
        return parentPath + File.separator + sandboxConfig.getJavaFileName();
    }

    @Override
    public ProcessExecuteInfo execute(File file, List<String> inputDataList) throws ExecuteCodeException {
        String containerId = null;
        try{
            // 1. 先编译成子节码
            boolean compile = compile(file);
            if(!compile){
                ProcessExecuteInfo executeInfo = new ProcessExecuteInfo();
                executeInfo.setErrorMessage("Compile Error");
                executeInfo.setExitValue(ExecuteStatus.EXECUTE_CODE_ERROR.getStatus());
                return executeInfo;
            }
            // 2. 再放到容器中执行
            // 2.1 创建容器
            containerId = initContainer(file);
            // 2.2 启动容器
            dockerClient.startContainerCmd(containerId).exec();
            // 2.3 执行命令
            return executeCmd(containerId, inputDataList);
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
                sandboxConfig.getImageName()
        );
        HostConfig hostConfig = new HostConfig();
        hostConfig.withMemory(sandboxConfig.getMaxMemory());
        hostConfig.withCpuCount(sandboxConfig.getCpuCount());
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
     * docker exec -it c9f /bin/sh -c "java -cp /app Solution < /app/*.in"
     * @param containerId
     * @return
     */
    private ProcessExecuteInfo executeCmd(String containerId, List<String> inputDataList) {
        // 获取Java程序的文件名称
        String fileName = sandboxConfig.getJavaFileName();
        int endIndex = fileName.lastIndexOf(".java");
        String cmdFileName = fileName.substring(0, endIndex);

        ProcessExecuteInfo processExecInfo = new ProcessExecuteInfo();
        // 输出结果
        List<String> outputDataList = new ArrayList<>();
        // 最大时间
        Long maxTime = 0L;
        // 最大内存
        Long maxMemory = 0L;
        for (int i = 0; i < inputDataList.size(); i++ ){
            String[] args = new String[]{"sh","-c","java -cp /app " + cmdFileName + " < /app/"+(i+1)+".in"};
            // 创建指令执行对象
            ExecCreateCmdResponse execCmdResponse =
                   DockerUtils.createCmdResponse(dockerClient, args, containerId);
            // 获取Docker的执行进程信息
            CmdExecuteInfo cmdExecuteInfo = DockerUtils.getProcessExecInfo(
                   execCmdResponse,
                   dockerClient,
                   containerId,
                   sandboxConfig.getMaxTime()
            );
            String errMessage = cmdExecuteInfo.getErrMessage();
            if(errMessage != null){
                processExecInfo.setErrorMessage(errMessage);
                processExecInfo.setExitValue(ExecuteStatus.EXECUTE_SYSTEM_ERROR.getStatus());
                return processExecInfo;
            }
            maxTime = Math.max(cmdExecuteInfo.getTime(),maxTime);
            maxMemory = Math.max(cmdExecuteInfo.getMemory(),maxMemory);
            outputDataList.add(cmdExecuteInfo.getMessage());
        }
        processExecInfo.setTime(maxTime);
        processExecInfo.setMemory(maxMemory);
        processExecInfo.setExecuteResult(outputDataList);
        processExecInfo.setExitValue(ExecuteStatus.EXECUTE_OK.getStatus());
        return processExecInfo;
    }

}
