package com.test.controller;

import com.mvc.core.result.Model;
import com.mvc.support.annotation.Autowired;
import com.mvc.support.annotation.RequestMapping;
import com.mvc.support.annotation.Screw;
import com.test.service.TestService;
import com.test.service.UserService;
import com.test.service.impl.Test;
import com.test.vo.user.UserParam;

@Screw
@RequestMapping("/login")
public class LoginController {

	@Autowired
	private UserService userService;
	@Autowired
	private TestService testService;
	@Autowired
	private Test test;

	@RequestMapping("/index")
	public String index() {
		return "/index";
	}

	@RequestMapping("/checkLogin")
	public String checkLogin( String username,UserParam sss,Model model, Double doubleBaozhuang, Float c, byte[] as, int[] ss, String[] aaa) {
		System.out.println(username);
		boolean result = userService.checkLogin(sss);
		model.addAttribute("_param", sss);
		if (result) {
			return "/ok";
		}
		return "/no";
	}
}
