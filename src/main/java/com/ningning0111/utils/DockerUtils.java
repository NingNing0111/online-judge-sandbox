package com.ningning0111.utils;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.api.command.StatsCmd;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.api.model.Statistics;
import com.github.dockerjava.api.model.StreamType;
import com.github.dockerjava.core.command.ExecStartResultCallback;
import com.ningning0111.model.ProcessExecuteInfo;
import com.ningning0111.model.enums.ExecuteStatus;
import org.springframework.util.StopWatch;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @Project: com.ningning0111.utils
 * @Author: pgthinker
 * @GitHub: https://github.com/ningning0111
 * @Date: 2024/3/1 13:13
 * @Description:
 */
public class DockerUtils {
    /**
     * 创建Docker指令执行对象
     * @param dockerClient
     * @param args
     * @param containerId
     * @return
     */
    public static ExecCreateCmdResponse createCmdResponse(DockerClient dockerClient, String[] args, String containerId){
        String[] cmdArray = ArrayUtil.append(args);
        ExecCreateCmdResponse execCmdResponse = dockerClient.execCreateCmd(containerId)
                .withCmd(cmdArray)
                .withAttachStderr(true)
                .withAttachStdin(true)
                .withAttachStdout(true)
                .exec();
        return execCmdResponse;
    }

    public static ProcessExecuteInfo getProcessExecInfo(
            ExecCreateCmdResponse execCmdResponse,
            DockerClient dockerClient,
            String containerId,
            long maxTime,
            TimeUnit timeUnit
    ) {
        if(timeUnit != TimeUnit.MILLISECONDS){
            maxTime = timeUnit.toMillis(maxTime);
        }
        StopWatch stopWatch = new StopWatch();
        ProcessExecuteInfo executeInfo = new ProcessExecuteInfo();
        final String[] errMessage = {null};
        // 记录程序执行耗时
        long time = 0L;
        // 判断程序是否正常执行
        // 程序执行结果
        final String[] result = {""};

        // 判断是否超时
        String execId = execCmdResponse.getId();
        ExecStartResultCallback execStartResultCallback = new ExecStartResultCallback(){
            @Override
            public void onNext(Frame frame) {
                StreamType streamType = frame.getStreamType();
                // 异常
                if(StreamType.STDERR.equals(streamType)){
                    errMessage[0] = new String(frame.getPayload());
                }else{
                    result[0] = StrUtil.format("{}{}",result[0],new String(frame.getPayload()));
                }
                super.onNext(frame);
            }
        };
        // 获取内存数据
        final Long[] maxMemory = {0L};
        StatsCmd statsCmd = dockerClient.statsCmd(containerId);
        ResultCallback<Statistics> callback = statsCmd.exec(new ResultCallback<Statistics>() {
            @Override
            public void onStart(Closeable closeable) {

            }

            @Override
            public void onNext(Statistics object) {
                maxMemory[0] = Math.max(object.getMemoryStats().getUsage(),maxMemory[0]);
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onComplete() {

            }

            @Override
            public void close() throws IOException {

            }
        });

        statsCmd.exec(callback);
        try {
            stopWatch.start();
            dockerClient.execStartCmd(execId)
                    .exec(execStartResultCallback)
                    .awaitCompletion(
                            maxTime,
                            timeUnit
                    );
            stopWatch.stop();
            time = stopWatch.getLastTaskTimeMillis();
            statsCmd.close();
        }catch (InterruptedException e){
            errMessage[0] = e.getMessage();
        }
        executeInfo.setMessage(result[0]);
        executeInfo.setErrorMessage(errMessage[0]);
        executeInfo.setTime(time);
        executeInfo.setMemory(maxMemory[0]);
        return executeInfo;
    }


}
