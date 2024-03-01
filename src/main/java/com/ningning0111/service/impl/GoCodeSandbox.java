package com.ningning0111.service.impl;

import com.ningning0111.exception.ExecuteCodeException;
import com.ningning0111.model.ProcessExecuteInfo;
import com.ningning0111.service.AbstractCodeSandbox;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * @Project: com.ningning0111.service.impl
 * @Author: pgthinker
 * @GitHub: https://github.com/ningning0111
 * @Date: 2024/3/2 01:43
 * @Description:
 */
@RequiredArgsConstructor
@Component("goCodeSandbox")
@Slf4j
public class GoCodeSandbox extends AbstractCodeSandbox {

    // todo: Go在容器中的代码实现

    @Override
    public String getCodeDirPath(String workDir) {
        return null;
    }

    @Override
    public String getCodeFilePath(String parentPath) {
        return null;
    }

    @Override
    public ProcessExecuteInfo execute(File file) throws ExecuteCodeException {
        return null;
    }
}
