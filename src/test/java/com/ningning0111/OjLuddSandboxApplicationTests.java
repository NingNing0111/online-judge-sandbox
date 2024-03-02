package com.ningning0111;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Image;
import com.ningning0111.config.SandboxFullConfig;
import com.ningning0111.model.ExecuteCodeRequest;
import com.ningning0111.model.ExecuteCodeResponse;
import com.ningning0111.service.SandboxService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@SpringBootTest
@Slf4j
class OjLuddSandboxApplicationTests {
	@Autowired
	private SandboxFullConfig sandboxConfig;
	@Autowired
	private SandboxService sandboxService;
	@Test
	public void testSandbox(){
		ExecuteCodeRequest request = new ExecuteCodeRequest();
		request.setLanguage("java");
		request.setCode("import java.util.Scanner;\n" +
				"\n" +
				"public class Solution {\n" +
				"    public static void main(String[] args) {\n" +
				"        Scanner scanner = new Scanner(System.in);\n" +
				"        \n" +
				"        int n = scanner.nextInt();\n" +
				"        while(n != 0){\n" +
				"            int i = scanner.nextInt();\n" +
				"             System.out.println(i);\n" +
				"            n--;\n" +
				"        }\n" +
				"    }\n" +
				"}\n");
		String input = "3\n1\n2\n4";
		request.setInput(Arrays.asList(input,"3\n2\n3\n10"));
		ExecuteCodeResponse execute = sandboxService.execute(request);
		System.out.println(execute);
	}
}
