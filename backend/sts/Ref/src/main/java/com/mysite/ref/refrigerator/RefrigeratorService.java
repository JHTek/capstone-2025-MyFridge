package com.mysite.ref.refrigerator;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mysite.ref.dto.RefrigeratorDTO;
import com.mysite.ref.dto.RefrigeratorResponseDTO;
import com.mysite.ref.user.JWTUtil;
import com.mysite.ref.user.Users;
import com.mysite.ref.user.UsersRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefrigeratorService {
	
	private final RefrigeratorRepository refrigeratorRepository;
	private final UsersRepository usersRepository;
	private final JWTUtil jwtutil;
	private final UserRefCRepository userRefCRepository;
	
	//냉장고 생성
	@Transactional
	public void createRef(RefrigeratorDTO dto, String token) {
		
		//dto -> entity
		Refrigerator refrigerator = new Refrigerator();
		refrigerator.setRefrigeratorName(dto.getRefrigeratorName());
		
		refrigeratorRepository.save(refrigerator); // DB에 저장
		
		
		//user 엔티티 가져오기
		String userId = jwtutil.getUserid(token);
	    Users user = usersRepository.findById(userId)
	            .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다."));
	    
	    //연관관계 설정
	    UserRefC userRefC = new UserRefC();
	    userRefC.setUserAndRefrigerator(user, refrigerator, 0);
	    //연관관계 업데이트
	    userRefCRepository.save(userRefC);
	    usersRepository.save(user);
	}
	
	//냉장고 리스트
	@Transactional
	public List<RefrigeratorResponseDTO> listRef(String token){
		
		String userId = jwtutil.getUserid(token);
		
		List<Refrigerator> refrigerators = userRefCRepository.findRefrigeratorsByUserId(userId);
		
		List<RefrigeratorResponseDTO> dtos = new ArrayList<>();
		for (Refrigerator refrigerator : refrigerators) {
			RefrigeratorResponseDTO dto = new RefrigeratorResponseDTO();
			dto.setRefrigeratorId(refrigerator.getRefrigeratorId());
			dto.setRefrigeratorName(refrigerator.getRefrigeratorName());
			
			dtos.add(dto);
		}
		
		return dtos;
	}
	
	//냉장고 읽기
	@Transactional
	public RefrigeratorResponseDTO readRefrigerator(int id) {
		Refrigerator refrigerator = refrigeratorRepository.findById(id).orElseThrow();
		
		RefrigeratorResponseDTO dto = new RefrigeratorResponseDTO();
		dto.setRefrigeratorId(refrigerator.getRefrigeratorId());
		dto.setRefrigeratorName(refrigerator.getRefrigeratorName());
		
		return dto;
	}
	

}
