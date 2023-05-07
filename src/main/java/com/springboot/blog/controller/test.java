package com.springboot.blog.controller;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class test {
	public static void main(String[] args) {
		BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
		System.out.println(bCryptPasswordEncoder.encode("admin"));
	}
}
