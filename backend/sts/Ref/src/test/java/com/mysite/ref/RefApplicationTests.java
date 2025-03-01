package com.mysite.ref;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.mysite.ref.user.Users;
import com.mysite.ref.user.UsersRepository;

import org.springframework.beans.factory.annotation.Autowired;

@SpringBootTest
class RefApplicationTests {

	@Autowired
	private UsersRepository usersRepository;
	
	@Test
	void testJpa() {
		Users u1 = new Users();
		u1.setPassword("1234");
		u1.setUsername("user1");
		u1.setUserid("user1");
		this.usersRepository.save(u1);
	}
	void contextLoads() {
	}

}
