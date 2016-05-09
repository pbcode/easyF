package com.mvc.core.mapping;

/**
 * @author god
 *         <p>
 *         ������URL��Ӧ��������������У������ĳ���ģ��
 *         </p>
 */
public class RequestMappingParam {
	// ��������
	private String paramName;
	// ��������Class
	private Class<?> paramType;
	// JNI������������
	private String jniBasicType;

	public String getJniBasicType() {
		return jniBasicType;
	}

	public void setJniBasicType(String jniBasicType) {
		this.jniBasicType = jniBasicType;
	}

	public String getParamName() {
		return paramName;
	}

	public void setParamName(String paramName) {
		this.paramName = paramName;
	}

	public Class<?> getParamType() {
		return paramType;
	}

	public void setParamType(Class<?> paramType) {
		this.paramType = paramType;
	}

}
