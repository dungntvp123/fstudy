package com.project.fstudy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.project.fstudy.data.dto.request.RegisterRequestDto;
import com.project.fstudy.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
class FstudyApplicationTests {

	@Autowired
	private AuthService authService;

	@Test
	void contextLoads() {
	}

	@Test
	void register() throws JsonProcessingException {
		RegisterRequestDto dto = new RegisterRequestDto("username1", "password", "email1@gmail.com");
		String token = (String) authService.register(dto).getBody();
		log.info(token);
		assert token != null;
	}


}
