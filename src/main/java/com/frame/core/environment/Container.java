package com.frame.core.environment;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import com.mvc.core.mapping.RequestMappingParam;

/**
 * 此类为容器对象类
 * 
 * @author god
 *
 */
public class Container {

	// URL与具体controller对应关系容器
	// 第一个String对应类级别的请求路径，第二个String对应类的全限定名，第三个String对应方法级别的请求路径，Method对应对应的请求的具体方法
	private Map<String, Map<String, Map<String, Method>>> classMappingMap;
	// Key为类的全限定名，Value为该类的实例对象
	private Map<String, Object> objectMap;
	// URL与具体Controller对应的方法，与方法参数对应关系的容器
	private Map<String, Map<Method, List<RequestMappingParam>>> methodParamMappingMap;

	public Map<String, Map<Method, List<RequestMappingParam>>> getMethodParamMappingMap() {
		return methodParamMappingMap;
	}

	public void setMethodParamMappingMap(Map<String, Map<Method, List<RequestMappingParam>>> methodParamMappingMap) {
		this.methodParamMappingMap = methodParamMappingMap;
	}

	public Map<String, Map<String, Map<String, Method>>> getClassMappingMap() {
		return classMappingMap;
	}

	public void setClassMappingMap(Map<String, Map<String, Map<String, Method>>> classMappingMap) {
		this.classMappingMap = classMappingMap;
	}

	public Map<String, Object> getObjectMap() {
		return objectMap;
	}

	public void setObjectMap(Map<String, Object> objectMap) {
		this.objectMap = objectMap;
	}

}
