package com.mysite.ref.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepository extends JpaRepository<Users, String> {
	boolean existsByUserid(String userid);
	
	Users findByUserid(String userid);
}
