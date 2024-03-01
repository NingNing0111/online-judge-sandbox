package com.ningning0111.controller;

import com.ningning0111.model.ExecuteCodeRequest;
import com.ningning0111.model.ExecuteCodeResponse;
import com.ningning0111.service.SandboxService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Project: com.ningning0111.controller
 * @Author: pgthinker
 * @GitHub: https://github.com/ningning0111
 * @Date: 2024/3/1 19:11
 * @Description:
 */
@RestController
@RequiredArgsConstructor
public class SandboxController {
    private final SandboxService sandboxService;
    @PostMapping("/execute")
    public ExecuteCodeResponse execute(
            @RequestBody ExecuteCodeRequest executeCodeRequest
    ){
        if(executeCodeRequest == null){
            throw new RuntimeException("请求参数为空");
        }
        return sandboxService.execute(executeCodeRequest);
    }
}
