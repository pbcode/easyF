package com.frame.core.aop;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class ProxyHandler implements InvocationHandler {

	private Object o;

	public ProxyHandler(Object o) {
		this.o = o;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		System.out.println("前置信息");
		method.invoke(o, args);
		System.out.println("后置信息");
		return null;
	}

}
