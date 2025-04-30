package com.mysite.ref.user;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UsersService {
	private final UsersRepository usersRepository;
	private final PasswordEncoder passwordEncoder;
	
	public Users create(String username, String userid, String password) {
		if (usersRepository.existsByUserid(userid)) {
	        throw new IllegalArgumentException("이미 존재하는 사용자 ID입니다.");
	    }
		Users user = new Users();
		user.setUserid(userid);
		user.setUsername(username);
		user.setRole("ROLE_USER"); // ✅ 일반 사용자 권한으로 설정 user.setRole("ROLE_ADMIN");는 관리자 권한
		user.setPassword(passwordEncoder.encode(password));
		this.usersRepository.save(user);
		return user;
		
	}
	
	public boolean checkUser(String userid) {
	    return usersRepository.existsByUserid(userid);
	}

	

}
