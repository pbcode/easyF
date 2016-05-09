package com.mvc.core.mapping;

/**
 * @author god
 *         <p>
 *         容器中URL对应方法与参数容器中，参数的抽象模型
 *         </p>
 */
public class RequestMappingParam {
	// 参数名称
	private String paramName;
	// 参数类型Class
	private Class<?> paramType;
	// JNI基本数据类型
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
