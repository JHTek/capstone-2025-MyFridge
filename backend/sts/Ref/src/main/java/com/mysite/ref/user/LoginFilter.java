package com.mysite.ref.user;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


public class LoginFilter extends UsernamePasswordAuthenticationFilter {
	
	private final AuthenticationManager authenticationManager;
	private final JWTUtil jwtUtil;
	
	public LoginFilter(AuthenticationManager authenticationManager,JWTUtil jwtUtil) {
		this.authenticationManager = authenticationManager;
		this.jwtUtil = jwtUtil;
	}
	
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException{
		
		String userid = obtainUserid(request);
		String password = obtainPassword(request);

		
		UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userid, password, null);
		
		return authenticationManager.authenticate(authToken);
		
	}
	
	@Override
	protected void successfulAuthentication(HttpServletRequest request,HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException {
		System.out.println("Success");
		CustomUserDetails customUserDetails = (CustomUserDetails)authentication.getPrincipal();
		String userid = customUserDetails.getUserid();
		String username = customUserDetails.getUsername();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();
        String token = jwtUtil.createJwt(userid, role, 1000L * 60 * 60 * 24);
        response.addHeader("Authorization", "Bearer " + token);
        response.setCharacterEncoding("UTF-8");
        
        response.setContentType("application/json");
        response.getWriter().write(
        	    "{"
        	    + "\"status\": \"success\","
        	    + "\"message\": \"로그인 성공\","
        	    + "\"userid\": \"" + userid + "\","
        	    + "\"username\": \"" + username + "\","
        	    + "\"role\": \"" + role + "\""
        	    + "}"
        	);
	}
	
	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,AuthenticationException failed) {
		System.out.println("fail");
		response.setStatus(401);
	}
	
	protected String obtainUserid(HttpServletRequest request) {
		return request.getParameter("userid");
	}

}
