package com.ningning0111.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @Project: com.ningning0111.service
 * @Author: pgthinker
 * @GitHub: https://github.com/ningning0111
 * @Date: 2024/3/1 10:23
 * @Description:
 */
@Component
@Slf4j
public class SandboxFactory implements ApplicationContextAware {
    private ApplicationContext applicationContext;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public CodeSandbox createSandbox(String language){
        CodeSandbox sandbox = applicationContext.getBean(language + "CodeSandbox", CodeSandbox.class);
        log.info("create a sandbox： " + sandbox.getClass().getName());
        return sandbox;
    }
}
