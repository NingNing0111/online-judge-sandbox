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
import com.ningning0111.model.CmdExecuteInfo;
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

    /**
     * 获取Docker运行指令后的进程信息
     * @param execCmdResponse
     * @param dockerClient
     * @param containerId
     * @param maxTime 指令运行时的最大时间限制 单位milliseconds
     * @return 时间、内存、输出结果和错误信息
     */
    public static synchronized CmdExecuteInfo getProcessExecInfo(
            ExecCreateCmdResponse execCmdResponse,
            DockerClient dockerClient,
            String containerId,
            long maxTime
    ) {
        StopWatch stopWatch = new StopWatch();
        CmdExecuteInfo cmdExecuteInfo = new CmdExecuteInfo();
        final String[] errMessage = {null};
        // 记录程序执行耗时
        long time = 0L;
        // 程序执行结果
        final String[] result = {""};

        // 1. 判断是否超时
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
        // 2. 获取内存数据
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
                            TimeUnit.MILLISECONDS
                    );
            stopWatch.stop();
            time = stopWatch.getLastTaskTimeMillis();
            statsCmd.close();
        }catch (InterruptedException e){
            e.printStackTrace();
            errMessage[0] = e.getMessage();
        }

        cmdExecuteInfo.setMessage(result[0]);
        cmdExecuteInfo.setErrMessage(errMessage[0]);
        cmdExecuteInfo.setTime(time);
        cmdExecuteInfo.setMemory(maxMemory[0]);
        time = 0L;
        maxMemory[0] = 0L;
        return cmdExecuteInfo;
    }


}
