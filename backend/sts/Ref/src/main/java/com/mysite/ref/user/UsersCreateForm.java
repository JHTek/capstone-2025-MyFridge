package com.mysite.ref.user;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsersCreateForm {
	@Size(min = 3, max = 25)
	@NotEmpty(message = "사용자 ID는 필수 항목입니다.")
	private String userid;
	
	@NotEmpty(message="비밀번호는 필수 항목입니다.")
	private String password1;
	
	@NotEmpty(message="비밀번호는 확인은 필수 항목입니다.")
	private String password2;
	
	@NotEmpty(message="사용자 이름은 필수 항목입니다.")
	private String username;

}
