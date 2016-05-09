package com.test.service.impl;

import com.mvc.support.annotation.Screw;
import com.test.service.UserService;
import com.test.vo.user.UserParam;

@Screw
public class UserServiceImpl implements UserService {

	public boolean checkLogin(UserParam param) {
		if (param.getUsername().equals("admin") && param.getPassword().equals("123456"))
			return true;
		return false;
	}

}
