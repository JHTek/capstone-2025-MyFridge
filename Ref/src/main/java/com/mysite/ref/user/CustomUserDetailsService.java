package com.mysite.ref.user;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService{
	
	private final UsersRepository usersRepository;
	
	public CustomUserDetailsService(UsersRepository usersRepository) {
		this.usersRepository = usersRepository;
	}
	
	
	@Override
	public UserDetails loadUserByUsername(String userid) throws UsernameNotFoundException {
		
		Users userData = usersRepository.findByUserid(userid);
		
		if(userData != null) {
			return new CustomUserDetails(userData);
		}else {
			throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
		}
	}

}
