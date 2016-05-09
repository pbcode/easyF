package com.test.service.impl;

import com.mvc.support.annotation.Screw;
import com.test.service.TestService;

@Screw
public class TestServiceImpl implements TestService {

	@Override
	public void test() {
		System.out.println("test");
	}

}
