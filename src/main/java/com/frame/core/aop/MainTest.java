package com.frame.core.aop;

import java.lang.reflect.Proxy;

public class MainTest {
	public static void main(String[] args) {
		Pangzi pangzi = new Pangzi();
		ProxyHandler p = new ProxyHandler(pangzi);
		Eat e = (Eat) Proxy.newProxyInstance(pangzi.getClass().getClassLoader(), pangzi.getClass().getInterfaces(), p);
		e.eat();
	}
}
