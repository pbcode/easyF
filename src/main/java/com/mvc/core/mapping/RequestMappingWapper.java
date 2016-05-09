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
 *         ����URL���ӦController�Լ���������
 *         </p>
 */
public class RequestMappingWapper {

	// ����Url��Controller��Ӧ��ϵ��������һ��keyΪ�༶���URL���ڶ���keyΪclass��ȫ�޶�����������keyΪ���������URL��valueΪ��Ӧ�ķ���
	private static final Map<String, Map<String, Map<String, Method>>> classMappingMap = new LinkedHashMap<>();
	// ����Url��Controller�з�����Ӧ��ϵ��������һ��keyΪ���ȫ�޶������ڶ���URL��Ӧ�ķ�����valueΪ�����еĲ����б�
	private static final Map<String, Map<Method, List<RequestMappingParam>>> methodParamMappingMap = new LinkedHashMap<>();

	/**
	 * @param container
	 *            ����
	 *            <p>
	 *            ��ʼ��URL����
	 *            </p>
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public static void initClassMappingMap(Container container) {
		// ͨ�������������class
		Map<String, Object> targetClazz = container.getObjectMap();
		// ����RequestMappingע����
		Class<RequestMapping> annotationClass = RequestMapping.class;
		// ѭ������class
		Iterator<String> it = targetClazz.keySet().iterator();
		while (it.hasNext()) {
			// ȡ��ĳһclass
			Class<?> clazz = targetClazz.get(it.next()).getClass();
			// �����class��RequestMappingע�����õ�
			if (clazz.isAnnotationPresent(annotationClass)) {
				// ��ȡ�����RequestMappingע��
				RequestMapping req = clazz.getAnnotation(annotationClass);
				// ��ʼ������������һ��keyΪclass��ȫ�޶������ڶ���keyΪURL��methodΪURL��Ӧ�ķ���
				Map<String, Map<String, Method>> methodHashMap = null;
				// ��ȡ��ǰclass�����з���
				Method[] methods = clazz.getDeclaredMethods();
				// ���������Ϊ��
				if (methods != null) {
					// ����������
					methodHashMap = new LinkedHashMap<>();
					// ������������URL��Ӧ����
					Map<String, Method> methodMappingMap = new HashMap<>();
					// ������һ��URL��Ӧ�ķ�����valueΪ�����еĲ����б������
					Map<Method, List<RequestMappingParam>> methodParamMap = new LinkedHashMap<>();
					// ѭ����class�����з���
					for (Method method : methods) {
						// ����÷���Ҳ��RequsetMappingע������
						if (method.isAnnotationPresent(annotationClass)) {
							// ���������б�
							List<RequestMappingParam> paramList = new ArrayList<>();
							// ��ȡ�÷�����RequsetMappingע��
							RequestMapping methodReq = method.getAnnotation(annotationClass);
							// ��RequsetMappingע���ڵ�URL��Ϊkey����Ӧ��methodΪvalue����������
							methodMappingMap.put(methodReq.value(), method);
							// ��ȡ�������
							InputStream in = clazz.getResourceAsStream(clazz.getSimpleName() + ".class");
							// ����ASM�����ȡ��
							ClassReader reader = null;
							try {
								// ע��������
								reader = new ClassReader(in);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							// ����ASM��ڵ����
							ClassNode cn = new ClassNode();
							// ͨ����ȡ�����м���
							reader.accept(cn, 0);
							// ��ȡ��������з���
							List<MethodNode> list = (List<MethodNode>) cn.methods;
							// ��ȡ��JAVA�����õķ����Ĳ��������б�
							Class<?>[] parameterTypes = method.getParameterTypes();
							// ����ASMȡ�õķ����б�
							for (MethodNode o : list) {
								// ���ASMȡ�õķ��������뷴���õķ���������ͬ
								if (o.name.equals(method.getName())) {
									// ������ASMȡ�õķ�����ע���б�
									List<AnnotationNode> annList = (List<AnnotationNode>) o.visibleAnnotations;
									// �������б�
									for (AnnotationNode annotationNode : annList) {
										// ��ȡע���ַ�����ȥ������";"����ǰ���L
										String annotationNodeType = annotationNode.desc.substring(1,
												annotationNode.desc.length() - 1);
										// ��"/"�滻Ϊ","
										annotationNodeType = annotationNodeType.replace("/", ".");
										// �����ע��ΪRequsetMapping
										if (annotationNodeType.equals(RequestMapping.class.getName())) {
											// ��������õ�ע��������ASM��ȡ��ע��������ͬ
											if (methodReq.value().equals(annotationNode.values.get(1))) {
												// ��ȡASM��õķ����Ĳ����б�
												List<LocalVariableNode> paramlist = o.localVariables;
												// ����ASM��õķ����Ĳ����б�
												for (int i = 0; i < paramlist.size(); i++) {
													// ȡ��Ĭ�ϵ�һ��this����������result�����������뿴class�ֽ���ķ��������б����ݣ�
													if (i == 0 || i == paramlist.size() - 1) {
														continue;
													}
													// ��ȡASM��õĲ���
													LocalVariableNode param = paramlist.get(i);
													// �������������ĳ���ģ��
													RequestMappingParam methodParam = new RequestMappingParam();
													// ע������
													methodParam.setParamName(param.name);
													// ע����������
													methodParam.setJniBasicType(param.desc);
													// �������õķ�����������ע��
													methodParam.setParamType(parameterTypes[i - 1]);
													// ��������б�
													paramList.add(methodParam);
												}
											}
										}
									}

								}
							}
							// ����URL��Ӧ�����Ĳ���������
							methodParamMap.put(method, paramList);
						}

					}
					// ��׼���ġ�URL��Ӧ���������������������������keyΪclass��ȫ�޶�����valueΪ��URL��Ӧ���������������
					methodParamMappingMap.put(clazz.getName(), methodParamMap);
					// ����URL��Ӧ�����Ĳ�������
					methodHashMap.put(clazz.getName(), methodMappingMap);
				}
				// ����classMappingMap������
				classMappingMap.put(req.value(), methodHashMap);
			}
		}
		// �������յ�����
		container.setClassMappingMap(classMappingMap);
		// �������յ�����
		container.setMethodParamMappingMap(methodParamMappingMap);
	}

}
