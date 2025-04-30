package com.mysite.ref.refrigerator;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mysite.ref.dto.RefrigeratorDTO;
import com.mysite.ref.dto.RefrigeratorResponseDTO;
import com.mysite.ref.user.JWTUtil;
import com.mysite.ref.user.UsersService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@RequestMapping("/refrigerator")
public class RefrigeratorController {
	
	private final RefrigeratorService refrigeratorService;
	private final JWTUtil jwtutil;
	
	@PostMapping("/create")
	public ResponseEntity<String> createRefrigerator(@RequestHeader("Authorization") String authorizationHeader, @RequestBody RefrigeratorDTO dto) {
		try {
			 System.out.println(">>> DTO received in controller: " + dto.getRefrigeratorName());
			 
			String token = authorizationHeader.replace("Bearer ", "").trim();
			refrigeratorService.createRef(dto, token);
			return ResponseEntity.ok("냉장고가 성공적으로 생성되었습니다.");
		}catch(Exception e) {
			return ResponseEntity.status(500).body("냉장고 추가에 실패했습니다. " + e.getMessage());
		}
		
	}
	
	@GetMapping("/list")
	public ResponseEntity<List<RefrigeratorResponseDTO>> getlist(@RequestHeader("Authorization") String authorizationHeader) {
		String token = authorizationHeader.replace("Bearer ", "").trim();
		List<RefrigeratorResponseDTO> refrigerators = refrigeratorService.listRef(token);
        return ResponseEntity.ok(refrigerators);
	}

}
