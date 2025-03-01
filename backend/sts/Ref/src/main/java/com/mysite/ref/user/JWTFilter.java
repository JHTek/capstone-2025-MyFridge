package com.mysite.ref.user;

import java.io.IOException;

import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JWTFilter extends OncePerRequestFilter{
	
	private final JWTUtil jwtUtil;
	
	public JWTFilter(JWTUtil jwtUtil) {
		this.jwtUtil = jwtUtil;
	}
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
	    if (request.getServletPath().equals("/login")) {
	        filterChain.doFilter(request, response);
	        return;
	    }
	    
		String authorization = request.getHeader("Authorization");
		
		if(authorization == null||!authorization.startsWith("Bearer ")) {
			System.out.println("token null");
			filterChain.doFilter(request, response);
			
			return;
		}
		
		String token = authorization.split(" ")[1];
		
		//소멸시간 검증
	    try {
	        if (jwtUtil.isExpired(token)) {
	            System.out.println("token expired");
	            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
	            response.getWriter().write("Token has expired");
	            return;
	        }
	    } catch (ExpiredJwtException e) {
	        System.out.println("token expired");
	        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
	        response.getWriter().write("Token has expired");
	        return;
	    } catch (Exception e) {
	        System.out.println("Invalid token");
	        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
	        response.getWriter().write("Invalid token");
	        return;
	    }
		
        String userid = jwtUtil.getUserid(token);
        String role = jwtUtil.getRole(token);
        
        Users user = new Users();
        user.setUserid(userid);
        user.setRole(role);
        user.setPassword("dummy");
        
        CustomUserDetails customUserDetails = new CustomUserDetails(user);
        
        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        
        SecurityContextHolder.getContext().setAuthentication(authToken);
        
        filterChain.doFilter(request, response);
	}
	

}
