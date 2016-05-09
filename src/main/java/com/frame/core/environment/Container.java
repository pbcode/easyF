package com.frame.core.environment;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import com.mvc.core.mapping.RequestMappingParam;

/**
 * ����Ϊ����������
 * 
 * @author god
 *
 */
public class Container {

	// URL�����controller��Ӧ��ϵ����
	// ��һ��String��Ӧ�༶�������·�����ڶ���String��Ӧ���ȫ�޶�����������String��Ӧ�������������·����Method��Ӧ��Ӧ������ľ��巽��
	private Map<String, Map<String, Map<String, Method>>> classMappingMap;
	// KeyΪ���ȫ�޶�����ValueΪ�����ʵ������
	private Map<String, Object> objectMap;
	// URL�����Controller��Ӧ�ķ������뷽��������Ӧ��ϵ������
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
