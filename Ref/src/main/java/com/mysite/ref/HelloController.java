package com.mysite.ref;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HelloController{
	@GetMapping("/jump")
	@ResponseBody
	public String hello() {
		return "안녕하세요 sbb에 오신것을 환영합니다.";
	}
	
	@GetMapping("/admin")
	@ResponseBody
	public String admin() {
		return "admin";
	}
}