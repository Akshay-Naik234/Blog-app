package com.springboot.blog.service.impl;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.springboot.blog.entity.Role;
import com.springboot.blog.entity.User;
import com.springboot.blog.exception.BlogAPIException;
import com.springboot.blog.payload.LoginDto;
import com.springboot.blog.payload.RegisterDto;
import com.springboot.blog.repository.RoleRepository;
import com.springboot.blog.repository.UserRepository;
import com.springboot.blog.security.JwtTokenProvider;
import com.springboot.blog.service.AuthService;

@Service
public class AuthServiceImpl implements AuthService {

	private UserRepository userRepository;
	private RoleRepository roleRepository;
	private PasswordEncoder passwordEncoder;
	
	private AuthenticationManager authenticationManager;
	
	private JwtTokenProvider jwtTokenProvider;
	
	@Autowired
	public AuthServiceImpl(AuthenticationManager authenticationManager,
			UserRepository userRepository,
			RoleRepository roleRepository,
			PasswordEncoder passwordEncoder,
			JwtTokenProvider jwtTokenProvider) {
		this.authenticationManager = authenticationManager;
		this.userRepository=userRepository;
		this.roleRepository=roleRepository;
		this.passwordEncoder=passwordEncoder;
		this.jwtTokenProvider=jwtTokenProvider;
	}

	@Override
	public String login(LoginDto loginDto) {

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginDto.getUsernameOrEmail(), loginDto.getPassword()));
        
        System.out.println("Authentication "+authentication);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        System.out.println("Securit context holder started ");
        
        String token = jwtTokenProvider.generateToken(authentication);
        System.out.println("token 1 "+token);
        return token;
    }

	@Override
	public String register(RegisterDto registerDto) {
		// Add check for username exists in database
		
		System.out.println("welcome to registered page");
		if(userRepository.existsByUsername(registerDto.getUsername())) {
			throw new BlogAPIException(HttpStatus.BAD_REQUEST, "Username is already exists !");
		}
		
		//add Check for email Exists
		
		if(userRepository.existsByEmail(registerDto.getEmail())) {
			throw new BlogAPIException(HttpStatus.BAD_REQUEST, "Email is already exists !");
		}
		
		User user = new User();
		System.out.println("name");
		user.setName(registerDto.getName());
		System.out.println("user name");
		user.setUsername(registerDto.getUsername());
		System.out.println("email");
		user.setEmail(registerDto.getEmail());
		System.out.println("password");
		user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
		
		Set<Role> roles = new HashSet<>();
		System.out.println("role");
		Role userRole = roleRepository.findByName("ROLE_USER").get();
		
		roles.add(userRole);
		System.out.println("role saved to user");
		user.setRoles(roles);
		System.out.println("user saved");
		userRepository.save(user);
		
		
		return "User registered successfully !";
	}

}
