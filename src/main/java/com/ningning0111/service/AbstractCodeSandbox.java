package com.ningning0111.service;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import com.ningning0111.exception.ExecuteCodeException;
import com.ningning0111.model.ExecuteCodeRequest;
import com.ningning0111.model.ExecuteCodeResponse;
import com.ningning0111.model.JudgeInfo;
import com.ningning0111.model.ProcessExecuteInfo;
import com.ningning0111.model.enums.ExecuteStatus;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;


/**
 * @Project: com.ningning0111.service
 * @Author: pgthinker
 * @GitHub: https://github.com/ningning0111
 * @Date: 2024/2/29 21:43
 * @Description: 代码沙箱的抽象实现类 采用模板方法模式
 */
@Slf4j
public abstract class AbstractCodeSandbox implements CodeSandbox{

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        List<String> inputDataList = executeCodeRequest.getInput();
        String code = executeCodeRequest.getCode();
        // 1. 将用户的代码和输入数据保存为文件
        File file = saveCodeToFile(code,inputDataList);

        // 2. 执行代码，获取输出结果
        ExecuteCodeResponse executeCodeResponse = null;
        try {
            ProcessExecuteInfo executeInfo = execute(file, inputDataList);
            // 3. 整理结果
            executeCodeResponse = getOutputResponse(executeInfo);
        }catch (Exception e){
            executeCodeResponse = ExecuteCodeResponse.builder()
                    .status(ExecuteStatus.EXECUTE_SYSTEM_ERROR.getStatus())
                    .message(e.getMessage())
                    .build();
        }finally {
            // 4. 文件清理
            boolean isDel = deleteFile(file);
            if(!isDel){
                log.error("deleteFile error, userCodeFilePath = {}", file.getAbsolutePath());
            }
        }

        return executeCodeResponse;
    }

    /**
     * 存储代码到本地的抽象实现
     * @param code
     * @Param inputData
     * @return File object
     */
    private File saveCodeToFile(String code,List<String> inputDataList){
        String workDir = System.getProperty("user.dir");
        String globalCodeDirPath = getCodeDirPath(workDir);
        if(!FileUtil.exist(globalCodeDirPath)){
            FileUtil.mkdir(globalCodeDirPath);
        }
        // 用户代码隔离存放
        String codeParentPath = globalCodeDirPath + File.separator + UUID.randomUUID();
        // 获取代码路径
        String codePath = getCodeFilePath(codeParentPath);

        // 存储代码
        File codeFile = FileUtil.writeString(code, codePath, StandardCharsets.UTF_8);

        int inputCount = 1;
        for (String inputData : inputDataList) {
            // 输入数据存储路径
            String inputPath = codeParentPath + File.separator + inputCount + ".in";
            // 写入文件中
            FileUtil.writeString(inputData,inputPath, CharsetUtil.CHARSET_UTF_8);
            inputCount++;
        }

        return codeFile;
    }

    public abstract String getCodeDirPath(String workDir);
    public abstract String getCodeFilePath(String parentPath);

    /**
     * 执行代码的抽象实现
     * @param file
     * @return List of ProcessExecuteInfo, each test case
     */
    public abstract ProcessExecuteInfo execute(File file, List<String> inputDataList) throws ExecuteCodeException;

    /**
     * 整理执行后的数据
     * @param processExecuteInfos
     * @return
     */
    private ExecuteCodeResponse getOutputResponse(ProcessExecuteInfo processExecuteInfos) {
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        String errorMessage = processExecuteInfos.getErrorMessage();
        // 如果存在执行用例过程中出现错误信息 则直接返回
        if(StrUtil.isNotBlank(errorMessage)){
            executeCodeResponse.setMessage(errorMessage);
            executeCodeResponse.setStatus(processExecuteInfos.getExitValue());
            executeCodeResponse.setJudgeInfo(null);
            executeCodeResponse.setOutput(null);
            return executeCodeResponse;
        }
        // 正常执行则获取执行结果
        List<String> executeResult = processExecuteInfos.getExecuteResult();
        executeCodeResponse.setOutput(executeResult);
        executeCodeResponse.setMessage(ExecuteStatus.EXECUTE_OK.name());
        executeCodeResponse.setStatus(processExecuteInfos.getExitValue());
        executeCodeResponse.setJudgeInfo(
                JudgeInfo.builder()
                        .time(processExecuteInfos.getTime())
                        .memory(processExecuteInfos.getMemory())
                        .build()
        );
        // 正常执行情况
        return executeCodeResponse;
    }

    /**
     * 删除代码文件
     * @param codeFile
     * @return 文件是否删除
     */
    private boolean deleteFile(File codeFile) {
        if(codeFile.getParentFile() != null){
            File codeAbsoluteFile = codeFile.getParentFile().getAbsoluteFile();
            return FileUtil.del(codeAbsoluteFile);
        }
        return false;
    }
}
