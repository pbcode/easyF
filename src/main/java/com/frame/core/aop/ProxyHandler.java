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
		System.out.println("ǰ����Ϣ");
		method.invoke(o, args);
		System.out.println("������Ϣ");
		return null;
	}

}
