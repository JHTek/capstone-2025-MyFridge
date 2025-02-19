package com.mysite.ref.user;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
public class UsersController {
	
	private final UsersService usersService;
	
	@PostMapping("/register")
	public ResponseEntity<String> registerUser(@RequestBody UserDTO userDTO){
		try {
			Users createdUser = usersService.create(userDTO.getUsername(), userDTO.getUserid(), userDTO.getPassword());
			return ResponseEntity.ok("회원가입 성공");//200
		}catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());//400
		}
	}
	
	@GetMapping("/checkUser")
	public ResponseEntity<Boolean> checkUser(@RequestParam("userid") String userid) {
	    boolean exists = usersService.checkUser(userid);
	    return ResponseEntity.ok(exists);
	}

}
