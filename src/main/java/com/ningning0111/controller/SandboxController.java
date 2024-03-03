package com.ningning0111.controller;

import cn.hutool.core.util.StrUtil;
import com.ningning0111.config.SandboxConfig;
import com.ningning0111.model.ExecuteCodeRequest;
import com.ningning0111.model.ExecuteCodeResponse;
import com.ningning0111.model.enums.ExecuteStatus;
import com.ningning0111.service.SandboxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @Project: com.ningning0111.controller
 * @Author: pgthinker
 * @GitHub: https://github.com/ningning0111
 * @Date: 2024/3/1 19:11
 * @Description:
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class SandboxController {
    private final SandboxService sandboxService;
    // 定义鉴权请求头和密钥
    private static final String AUTH_REQUEST_HEADER = "auth";

    private final SandboxConfig sandboxConfig;

    @PostMapping("/execute")
    public ExecuteCodeResponse execute(
            @RequestBody ExecuteCodeRequest executeCodeRequest,
            HttpServletRequest request,
            HttpServletResponse response
    ){
        String authKey = request.getHeader(AUTH_REQUEST_HEADER);
        String secretKey = sandboxConfig.getSecretKey();
        // 如果配置了密钥
        if(StrUtil.isNotBlank(secretKey) && !StrUtil.equals(authKey,secretKey)){
            response.setStatus(403);
            return ExecuteCodeResponse.builder()
                    .message("身份校验错误")
                    .status(ExecuteStatus.EXECUTE_AUTH_ERROR.getStatus())
                    .build();
        }
        if(executeCodeRequest == null){
            throw new RuntimeException("请求参数为空");
        }
        return sandboxService.execute(executeCodeRequest);
    }
}
