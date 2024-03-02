package com.ningning0111.service.impl;

import com.ningning0111.exception.ExecuteCodeException;
import com.ningning0111.model.ProcessExecuteInfo;
import com.ningning0111.service.AbstractCodeSandbox;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

/**
 * @Project: com.ningning0111.service.impl
 * @Author: pgthinker
 * @GitHub: https://github.com/ningning0111
 * @Date: 2024/3/1 00:30
 * @Description:
 */
@RequiredArgsConstructor
@Component("cppCodeSandbox")
@Slf4j
public class CppCodeSandbox extends AbstractCodeSandbox {

    // todo: C++代码在容器中执行的实现

    @Override
    public String getCodeDirPath(String workDir) {
        return null;
    }

    @Override
    public String getCodeFilePath(String parentPath) {
        return null;
    }

    @Override
    public ProcessExecuteInfo execute(File file, List<String> inputDataList) throws ExecuteCodeException {
        return null;
    }
}
