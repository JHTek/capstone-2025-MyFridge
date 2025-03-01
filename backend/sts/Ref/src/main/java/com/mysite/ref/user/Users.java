package com.mysite.ref.user;

import java.util.ArrayList;
import java.util.List;

import com.mysite.ref.refrigerator.UserRefC;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
	
	@OneToMany(mappedBy = "user")
	private List<UserRefC> userRefC = new ArrayList<>();
	
}
