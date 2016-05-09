package com.mvc.core.mapping;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodNode;

import com.frame.core.environment.Container;
import com.mvc.support.annotation.RequestMapping;

/**
 * @author god
 *         <p>
 *         创建URL与对应Controller以及方法的类
 *         </p>
 */
public class RequestMappingWapper {

	// 创建Url与Controller对应关系容器，第一个key为类级别的URL，第二个key为class的全限定名，第三个key为方法级别的URL，value为对应的方法
	private static final Map<String, Map<String, Map<String, Method>>> classMappingMap = new LinkedHashMap<>();
	// 创建Url与Controller中方法对应关系容器，第一个key为类的全限定名，第二个URL对应的方法，value为方法中的参数列表
	private static final Map<String, Map<Method, List<RequestMappingParam>>> methodParamMappingMap = new LinkedHashMap<>();

	/**
	 * @param container
	 *            容器
	 *            <p>
	 *            初始化URL容器
	 *            </p>
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public static void initClassMappingMap(Container container) {
		// 通过容器获得所有class
		Map<String, Object> targetClazz = container.getObjectMap();
		// 创建RequestMapping注解类
		Class<RequestMapping> annotationClass = RequestMapping.class;
		// 循环所有class
		Iterator<String> it = targetClazz.keySet().iterator();
		while (it.hasNext()) {
			// 取得某一class
			Class<?> clazz = targetClazz.get(it.next()).getClass();
			// 如果该class被RequestMapping注解作用到
			if (clazz.isAnnotationPresent(annotationClass)) {
				// 获取该类的RequestMapping注解
				RequestMapping req = clazz.getAnnotation(annotationClass);
				// 初始化子容器，第一个key为class的全限定名，第二个key为URL，method为URL对应的方法
				Map<String, Map<String, Method>> methodHashMap = null;
				// 获取当前class的所有方法
				Method[] methods = clazz.getDeclaredMethods();
				// 如果方法不为空
				if (methods != null) {
					// 创建子容器
					methodHashMap = new LinkedHashMap<>();
					// 创建方法级别URL对应容器
					Map<String, Method> methodMappingMap = new HashMap<>();
					// 创建第一个URL对应的方法，value为方法中的参数列表的容器
					Map<Method, List<RequestMappingParam>> methodParamMap = new LinkedHashMap<>();
					// 循环该class的所有方法
					for (Method method : methods) {
						// 如果该方法也被RequsetMapping注解作用
						if (method.isAnnotationPresent(annotationClass)) {
							// 创建参数列表
							List<RequestMappingParam> paramList = new ArrayList<>();
							// 获取该方法的RequsetMapping注解
							RequestMapping methodReq = method.getAnnotation(annotationClass);
							// 将RequsetMapping注解内的URL做为key，对应的method为value存入容器中
							methodMappingMap.put(methodReq.value(), method);
							// 获取该类的流
							InputStream in = clazz.getResourceAsStream(clazz.getSimpleName() + ".class");
							// 创建ASM的类读取器
							ClassReader reader = null;
							try {
								// 注入该类的流
								reader = new ClassReader(in);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							// 创建ASM类节点组件
							ClassNode cn = new ClassNode();
							// 通过读取器进行加载
							reader.accept(cn, 0);
							// 获取该类的所有方法
							List<MethodNode> list = (List<MethodNode>) cn.methods;
							// 获取由JAVA反射获得的方法的参数类型列表
							Class<?>[] parameterTypes = method.getParameterTypes();
							// 迭代ASM取得的方法列表
							for (MethodNode o : list) {
								// 如果ASM取得的方法名称与反射获得的方法名称相同
								if (o.name.equals(method.getName())) {
									// 遍历该ASM取得的方法的注解列表
									List<AnnotationNode> annList = (List<AnnotationNode>) o.visibleAnnotations;
									// 迭代该列表
									for (AnnotationNode annotationNode : annList) {
										// 截取注解字符串，去掉最后的";"与最前面的L
										String annotationNodeType = annotationNode.desc.substring(1,
												annotationNode.desc.length() - 1);
										// 将"/"替换为","
										annotationNodeType = annotationNodeType.replace("/", ".");
										// 如果该注解为RequsetMapping
										if (annotationNodeType.equals(RequestMapping.class.getName())) {
											// 如果反射获得的注解内容与ASM获取的注解内容相同
											if (methodReq.value().equals(annotationNode.values.get(1))) {
												// 获取ASM获得的方法的参数列表
												List<LocalVariableNode> paramlist = o.localVariables;
												// 迭代ASM获得的方法的参数列表
												for (int i = 0; i < paramlist.size(); i++) {
													// 取得默认第一个this参数与最后的result参数（具体请看class字节码的方法参数列表内容）
													if (i == 0 || i == paramlist.size() - 1) {
														continue;
													}
													// 获取ASM获得的参数
													LocalVariableNode param = paramlist.get(i);
													// 创建容器参数的抽象模型
													RequestMappingParam methodParam = new RequestMappingParam();
													// 注入名称
													methodParam.setParamName(param.name);
													// 注入类型名称
													methodParam.setJniBasicType(param.desc);
													// 将反射获得的方法参数类型注入
													methodParam.setParamType(parameterTypes[i - 1]);
													// 存入参数列表
													paramList.add(methodParam);
												}
											}
										}
									}

								}
							}
							// 存入URL对应方法的参数子容器
							methodParamMap.put(method, paramList);
						}

					}
					// 将准备的“URL对应方法级别的容器”放入子容器，key为class的全限定名，value为“URL对应方法级别的容器”
					methodParamMappingMap.put(clazz.getName(), methodParamMap);
					// 存入URL对应方法的参数容器
					methodHashMap.put(clazz.getName(), methodMappingMap);
				}
				// 存入classMappingMap容器中
				classMappingMap.put(req.value(), methodHashMap);
			}
		}
		// 存入最终的容器
		container.setClassMappingMap(classMappingMap);
		// 存入最终的容器
		container.setMethodParamMappingMap(methodParamMappingMap);
	}

}
