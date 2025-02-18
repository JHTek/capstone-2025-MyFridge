package com.mysite.ref.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Users {
	@Id
	@Column(unique=true)
	private String userid;
	
	private String username;
	private String password;
	
	private String role;
	
}
