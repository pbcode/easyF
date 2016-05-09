package com.mvc.core.dispather;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.frame.core.environment.Container;
import com.frame.core.ioc.AnnotationIocBuilder;
import com.mvc.core.mapping.RequestMappingParam;
import com.mvc.core.result.Model;
import com.mvc.core.result.ModelRealization;

/**
 * @author god
 *         <p>
 *         ���ķַ��࣬�̳�HttpServlet������servlet3.0ʵ��������
 *         </p>
 */
@SuppressWarnings("serial")
@WebServlet(urlPatterns = { "/" })
public class DefaultDispather extends HttpServlet {

	// ͨ��IOC����������Ѿ��洢�����ݵ�����
	private final static Container container = AnnotationIocBuilder.getContainer();
	// Quest�д洢��ͼģ�͵�KEY
	private static String DISPATHER_RESULT_MODEL = "dispather_result_model";

	@Override
	public void init() throws ServletException {
		super.init();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// �̳�doPost����
		doPost(request, response);
	}

	/*
	 * ִ�зַ�ǰ��׼��������������ȡURL��Ӧ��controller��Ϊcontroller�����Ĳ�����ֵ��
	 * 
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.
	 * HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) {
		// ���URL��Controller֮���ϵ����
		// ��һ��String��Ӧ�༶�������·�����ڶ���String��Ӧ���ȫ�޶�����������String��Ӧ�������������·����Method��Ӧ��Ӧ������ľ��巽��
		Map<String, Map<String, Map<String, Method>>> mappingMap = container.getClassMappingMap();
		// ������ȫ�޶�������ʵ������
		Map<String, Object> objectMap = container.getObjectMap();
		// ��������з���������б��ϵ����
		Map<String, Map<Method, List<RequestMappingParam>>> methodParamMappingMap = container
				.getMethodParamMappingMap();
		// ��ȡ����·��
		String servletPath = request.getServletPath();
		// ����/���ָ�·��
		String[] servletPathArray = servletPath.split("/");
		// ����ָ���·�����鳤�ȴ�����
		if (servletPathArray.length > 0) {
			// ����һ����ֵ��controller�༶�������·��
			String classPath = servletPathArray[0];
			// �����������������·��
			String methodPath = "";
			// ��������һ��·��Ϊ��
			if (null == classPath || classPath.equals("")) {
				// ������ڶ�����ֵ����·��
				classPath = servletPathArray[1];
				// ���½���/���ַ��������·��
				classPath = "/" + classPath;
				// ��ʣ���·����ֵ�����������·��
				methodPath = servletPath.substring(classPath.length(), servletPath.length());
			}
			// ͨ���༶��·����ȡController���з�����������·�����Ӧ������ŵ�����
			// �ò�����Map�е�һ��String��Ӧ���ȫ�޶������ڶ���String��Ӧ�������������·����Method��Ӧ��Ӧ������ľ��巽��
			Map<String, Map<String, Method>> methodMappingMap = mappingMap.get(classPath);
			// ������ȡ���ȫ�޶���
			Set<String> classNameSet = methodMappingMap.keySet();
			Iterator<String> classNameIt = classNameSet.iterator();
			// ��ȡ��ȫ�޶������ϵĳ���
			int classSize = classNameSet.size();
			// ������ȴ���1
			if (classSize > 1) {
				try {
					// �׳��쳣����Ϊͬһ��URLֻ�ܶ�Ӧһ����
					throw new Exception("��ͬ��RequestMapping���ж����");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			// ��ȡURL��Ӧ�����ȫ�޶���
			String className = classNameIt.next();
			// ͨ��IOC��������ȡ�����ʵ������
			// ���������URL��Ӧ��controller��
			Object targetObject = objectMap.get(className);
			// ͨ��URL��Ӧ����ȫ�޶�����ȡ����������RquesetMapping�ķ���
			// ��Map��KeyΪ���������URL��MethodΪ��Ӧ��Method
			Map<String, Method> methodMap = methodMappingMap.get(className);
			// ͨ��controller���ȫ�޶�����ȡ���з���������б��ϵ����
			Map<Method, List<RequestMappingParam>> methodParamMap = methodParamMappingMap.get(className);
			// ͨ�����������URL��ȡ��Ӧ��Method
			Method method = methodMap.get(methodPath);
			// ���÷�������Ϊ�ɷ���
			method.setAccessible(true);
			// ��ȡ��ǰRequest���������еĲ���
			Map<String, String[]> requestParameterMap = request.getParameterMap();
			Set<Entry<String, String[]>> requestParamEntrySet = requestParameterMap.entrySet();
			// ����Request������Ӧ��set������Ϊ��ע�뵽controller�ķ�����׼��
			// Key��������Զ�Ӧ�����ƣ�Value��������Զ�Ӧ��set����
			// ���磨private String username�� usernameΪKey,setUsername����Ϊvalue
			Map<String, String> setMethodNameMap = new HashMap<>();
			// ������ǰRequset�����е����в���
			for (Entry<String, String[]> requesetParamEntry : requestParamEntrySet) {
				// Requset��������
				String requsetParamName = requesetParamEntry.getKey();
				// ��ȡ��Requset�����ĵ�һ����ĸ
				String requsetParamFirstStr = requsetParamName.substring(0, 1);
				// ƴ��request������Ӧ��set����
				requsetParamName = "set" + requsetParamFirstStr.toUpperCase()
						+ requsetParamName.substring(1, requsetParamName.length());
				// �����ɺõ�Requset������Ӧ��Set��������List����
				setMethodNameMap.put(requesetParamEntry.getKey(), requsetParamName);
			}
			// ��ȡ��ǰִ�е�Controller���з����Ĳ�������
			Class<?>[] methodParamTypes = method.getParameterTypes();
			// ����URL��ӦMethod�Ĳ����б�Ϊ���ø�Method��׼��
			Object[] args = new Object[methodParamTypes.length];
			// ����Controller���з����Ĳ������ͣ�������ʱֻ��Ϊ�������͵ģ�
			List<RequestMappingParam> paramList = methodParamMap.get(method);
			for (int i = 0; i < args.length; i++) {
				// ��ȡ����������һ��������Ӧ��Class
				RequestMappingParam param = paramList.get(i);
				// ��������Ĳ����б����ĳһ�������ͷ����еĲ���������ͬ�����
				if (requestParameterMap.containsKey(param.getParamName())) {
					// ��������в�����JNI��������Ϊ����
					if (param.getJniBasicType().startsWith("[")) {
						// TODO �������ʹ�����
					}
					// ��������в�����JNI��������Ϊ����
					if (param.getJniBasicType().startsWith("L")) {
						// ��������в�����JNI��������ΪJAVA��Lang���µİ�װ��
						if (param.getJniBasicType().startsWith("Ljava/lang")) {
							try {
								// ��ȡ�����Ϊ�ַ����Ĺ��췽������request�еĶ�Ӧ�Ĳ���ֵ�����ù��췽���У���ͨ���˹��췽���������󣬸�ֵ�������Ĳ����б�
								args[i] = param.getParamType().getConstructor(String.class)
										.newInstance(requestParameterMap.get(param.getParamName())[0]);
							} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
									| InvocationTargetException | NoSuchMethodException | SecurityException e) {
								e.printStackTrace();
							}
						} else {
							// ��������в�����JNI���Ͳ�ΪJAVA��Lang���µİ�װ�࣬����ȴ��Object�͵ģ�����request�Ĳ���Ϊ����������һ������
							args[i] = visitObjectFieldParam(param, setMethodNameMap, requestParameterMap);
						}
					}
					// ��������еĲ���JNI����Ϊ������
					if (param.getJniBasicType().equals("Z")) {
						// Stringתboolean
						args[i] = Boolean.getBoolean(requestParameterMap.get(param.getParamName())[0]);
					}
					// ��������еĲ���JNI����ΪByte
					if (param.getJniBasicType().equals("B")) {
						// Stringתbyte
						args[i] = Byte.parseByte(requestParameterMap.get(param.getParamName())[0]);
					}
					// ��������еĲ���JNI����Ϊchar
					if (param.getJniBasicType().equals("C")) {
						// ֻȡ�ø��ַ����ĵ�һ������ĸ
						try {
							args[i] = requestParameterMap.get(param.getParamName())[0].charAt(0);
							// ��ʾ
							throw new Exception("�����ڽ����Ĳ���" + param.getParamName() + "ת��char���ͣ���ֻ�������ĵ�һ�����ַ���");
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					// ��������еĲ���JNI����Ϊshort
					if (param.getJniBasicType().equals("S")) {
						// Stringתshort
						args[i] = Short.valueOf(requestParameterMap.get(param.getParamName())[0]);
					}
					// ��������в���JNI����Ϊint
					if (param.getJniBasicType().equals("I")) {
						// Stringתint
						args[i] = Integer.parseInt(requestParameterMap.get(param.getParamName())[0]);
					}
					// ��������в���JNI����Ϊlong
					if (param.getJniBasicType().equals("J")) {
						// Stringתlong
						args[i] = Long.parseLong(requestParameterMap.get(param.getParamName())[0]);
					}
					// ��������в���JNI����Ϊfloat
					if (param.getJniBasicType().equals("F")) {
						// Stringתfloat
						args[i] = Float.parseFloat(requestParameterMap.get(param.getParamName())[0]);
					}
					// ��������в���JNI����Ϊdouble
					if (param.getJniBasicType().equals("D")) {
						// Stringתdouble
						args[i] = Double.parseDouble(requestParameterMap.get(param.getParamName())[0]);
					}
				} else {
					// ��������Ĳ����б�����ĳһ�������ͷ����еĲ���������ͬ�����
					// �������������JNI����Ϊ����
					if (param.getJniBasicType().startsWith("[")) {
						// ��ʼ��Ϊnull
						args[i] = null;
						// ��������Ĳ�����JNI����ΪObject
					} else if (param.getJniBasicType().startsWith("L")) {
						// ��������Ĳ���Ϊjava.lang���еİ�װ��
						if (param.getJniBasicType().startsWith("Ljava/lang")) {
							try {
								// ��ʼ��Ϊnull
								args[i] = null;
							} catch (IllegalArgumentException | SecurityException e) {
								e.printStackTrace();
							}
						} else {
							// ��������еĲ���Ϊ��java.lang���µ�Object������Ϊrequest�ĸò���Ϊobject�е�����һ������
							args[i] = visitObjectFieldParam(param, setMethodNameMap, requestParameterMap);
						}
					} else {
						// ��������Ĳ����Ȳ��������ͣ�Ҳ����Object�ͣ�����Ϊ��������
						try {
							// ��ʾ���׳��쳣
							throw new Exception("���Ĳ���" + param.getParamName() + "������������û�б���ֵ�����Դ����Ƽ�ʹ����Ӧ�İ�װ��!");
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
			// ִ��controller��Ӧ������ǰ�ù�������ϣ�ִ��URL��Ӧ�ķ���
			invoke(method, targetObject, args, request, response);
		}
	}

	@Override
	public ServletContext getServletContext() {
		return super.getServletContext();
	}

	/**
	 * ִ��controller��ӦURL�ķ���
	 * 
	 * @param method
	 *            ��Ӧ��method����
	 * @param targetObject
	 *            ��Ӧ��controller
	 * @param args
	 *            method����ִ������Ҫ�Ĳ���
	 * @param request
	 * @param response
	 */
	private Object invoke(Method method, Object targetObject, Object[] args, HttpServletRequest request,
			HttpServletResponse response) {
		// ִ��url��Ӧ��method����������÷���ֵ
		Object returnObject = null;
		try {
			// ����method��������
			for (Object param : args) {
				// �������Ϊmodelģ��
				if (param instanceof Model) {
					// ��model����request��
					request.setAttribute(DISPATHER_RESULT_MODEL, param);
				}
			}
			// ִ�з�������ȡ����ֵ
			returnObject = method.invoke(targetObject, args);
			// ִ�����ַ�����
			render(returnObject, request, response);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return returnObject;
	}

	/**
	 * ���ݷ�������ִֵ�зַ�����
	 * 
	 * @param returnObject
	 *            ��Ӧ��method��������ֵ
	 * @param request
	 * @param response
	 */
	private void render(Object returnObject, HttpServletRequest request, HttpServletResponse response) {
		if (returnObject == null) {
			try {
				throw new Exception("URL��Ӧ����û�з���ֵ��");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// ȡ�ñ���request�����modelģ��
		Model model = (Model) request.getAttribute(DISPATHER_RESULT_MODEL);
		// ���model��Ϊ��
		if (model != null) {
			// ��ȡmodel�д������������
			Map<String, Object> modelAttributes = model.getAttributes();
			// ����model��������
			Set<Entry<String, Object>> modelEntry = modelAttributes.entrySet();
			Iterator<Entry<String, Object>> modelIt = modelEntry.iterator();
			while (modelIt.hasNext()) {
				Entry<String, Object> modelItem = modelIt.next();
				// ��model���������Ը��Ƹ�requset
				request.setAttribute(modelItem.getKey(), modelItem.getValue());
			}
		}
		// ���controller�з����ķ�������Ϊ�ַ���
		if (returnObject instanceof String) {
			// ǿת���ص��ַ���
			String returnUrl = (String) returnObject;
			// �����"/"��ͷ
			if (returnUrl.startsWith("/")) {
				// ȡ�ú�����ַ���
				returnUrl = returnUrl.substring(1, returnUrl.length());
			}
			// ƴ���ַ�����web·���µ�jsp
			returnUrl = "/WEB-INF/jsp/" + returnUrl + ".jsp";
			// ����ServletĬ�ϵķַ���
			RequestDispatcher requestDispatcher = request.getRequestDispatcher(returnUrl);
			// ִ��ת��
			try {
				requestDispatcher.forward(request, response);
			} catch (ServletException | IOException e) {
				e.printStackTrace();
			}
		}
		// TODO ��������Ϊ�����ģ�������

	}

	/**
	 * @param param
	 * @param setMethodNameMap
	 * @param requestParameterMap
	 * @return
	 *         <p>
	 *         request������Ϊmethod�в��������ԣ������ô˷������г�ʼ����method����
	 *         </p>
	 */
	public Object visitObjectFieldParam(RequestMappingParam param, Map<String, String> setMethodNameMap,
			Map<String, String[]> requestParameterMap) {
		// ��ȡ�÷����в���Class���������ԣ��������̳еģ�
		Class<?> methodClass = param.getParamType();
		Field[] fields = methodClass.getDeclaredFields();
		// �����ò�����ʵ�����󣬸�ֵΪNULL
		Object paramInstance = null;
		// ���controller�����еĲ���Ϊ�ӿ�����
		if (methodClass.isInterface()) {
			// ��������Ľӿ�����Ϊmodel�����Ҵ����ķ�����ͼģ�ͣ�����
			if (Model.class.getName().equals(methodClass.getName())) {
				// ��Modelʵ������д���
				try {
					paramInstance = ModelRealization.class.newInstance();
				} catch (InstantiationException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		} else {
			// �����������ͣ��򴴽�
			// TODO ������ʱֻ��Ϊ�������͵ģ��������ʹ�������
			try {
				paramInstance = methodClass.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		// �����÷����в���Class���������ԣ��������̳еģ�
		for (Field field : fields) {
			// ��ȡ��������
			String fieldName = field.getName();
			// ��ȡ���Զ�Ӧ������Class
			Class<?> fieldType = field.getType();
			// ����URL��Ӧ�����в�����Class�ķ���
			Method paramMethod = null;
			try {
				// ͨ��121�д�����Set�������ϻ�ȡ��
				// Key��������Զ�Ӧ�����ƣ�Value��������Զ�Ӧ��set����
				// ���磨private String username��
				// usernameΪKey,setUsername����Ϊvalue
				// ����fieldNameΪusername,��ȡ��methodName Ϊ
				// setUsername
				String methodName = setMethodNameMap.get(fieldName);
				// ���set����Ϊ�գ����жϵ�ǰѭ����������һ��ѭ��
				if (methodName == null) {
					continue;
				}
				// ���set�������ڣ����ȡ�ò���class�е�set����
				paramMethod = methodClass.getMethod(methodName, new Class[] { fieldType });
				// ����÷�������
				if (paramMethod != null) {
					// ���URL���õķ����Ĳ�����class�Ĳ�����request�����������ͬ
					if (requestParameterMap.containsKey(fieldName)) {
						// ���������б��ж�Ӧ��ʵ������
						Object[] paramObject = new Object[] { requestParameterMap.get(fieldName)[0] };
						try {
							// ����URL��Ӧ�����Ĳ�����set���������и�ֵ
							paramMethod.invoke(paramInstance, paramObject);
						} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
							e.printStackTrace();
						}
					}
				}
			} catch (NoSuchMethodException | SecurityException e) {
				// ��������ڸ÷��������Ѱ��
				continue;
			}
		}
		// ������ʵ������ֵ���뵽�����б�
		return paramInstance;
	}

}
