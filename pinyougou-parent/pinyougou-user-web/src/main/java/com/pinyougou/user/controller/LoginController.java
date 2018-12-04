package com.pinyougou.user.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public final class LoginController {

	@RequestMapping("/loginName")
	public Map<String, String> findLoginName() {
		Map map = new HashMap<>();
	String name = SecurityContextHolder.getContext().getAuthentication().getName();
	map.put("loginName", name);
	return map;
	}
}