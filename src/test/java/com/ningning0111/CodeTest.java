package com.ningning0111;

import com.ningning0111.model.ExecuteCodeRequest;
import com.ningning0111.model.ExecuteCodeResponse;
import com.ningning0111.service.SandboxService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

/**
 * @Project: com.ningning0111
 * @Author: pgthinker
 * @GitHub: https://github.com/ningning0111
 * @Date: 2024/3/1 16:24
 * @Description:
 */
@SpringBootTest
public class CodeTest {

    @Autowired
    private SandboxService sandboxService;

    @Test
    public void test1(){
        String code ="public class Solution {\n" +
                "    public static void main(String[] args) {\n" +
                "        while (true){\n" +
                "            System.out.print(\"....\");\n" +
                "        }\n" +
                "    }\n" +
                "}";
        ExecuteCodeResponse execute = sandboxService.execute(
                ExecuteCodeRequest.builder()
                        .code(code)
                        .language("java")
                        .input(Arrays.asList("123"))
                        .build()
        );
        System.out.println(execute);
    }

    @Test
    public void test2() {
        String code = "public class Solution {\n" +
                "    public static void main(String[] args) {\n" +
                "        System.out.println(\"Hello Wolrd\");\n" +
                "    }\n" +
                "}";
        ExecuteCodeResponse execute = sandboxService.execute(
                ExecuteCodeRequest.builder()
                        .code(code)
                        .language("java")
                        .input(Arrays.asList("123"))
                        .build()
        );
        System.out.println(execute);
    }
}
