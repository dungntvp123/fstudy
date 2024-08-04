package com.project.fstudy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.project.fstudy.data.dto.request.RegisterRequestDto;
import com.project.fstudy.repository.AccountRepository;
import com.project.fstudy.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Timestamp;

@SpringBootTest
@Slf4j
class FstudyApplicationTests {

	@Autowired
	private AccountRepository accountRepository;

	@Test
	void contextLoads() {
	}

	@Test
	void register() throws JsonProcessingException {
		Timestamp token = accountRepository.findAccountExpiredTimeByUsername("username1").get();
		log.info(token.toString());
	}


}
