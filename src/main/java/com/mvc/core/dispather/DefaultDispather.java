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
 *         核心分发类，继承HttpServlet，利用servlet3.0实现零配置
 *         </p>
 */
@SuppressWarnings("serial")
@WebServlet(urlPatterns = { "/" })
public class DefaultDispather extends HttpServlet {

	// 通过IOC构造器获得已经存储完数据的容器
	private final static Container container = AnnotationIocBuilder.getContainer();
	// Quest中存储视图模型的KEY
	private static String DISPATHER_RESULT_MODEL = "dispather_result_model";

	@Override
	public void init() throws ServletException {
		super.init();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// 继承doPost方法
		doPost(request, response);
	}

	/*
	 * 执行分发前的准备工作，包括获取URL对应的controller，为controller方法的参数赋值等
	 * 
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.
	 * HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) {
		// 获得URL与Controller之间关系容器
		// 第一个String对应类级别的请求路径，第二个String对应类的全限定名，第三个String对应方法级别的请求路径，Method对应对应的请求的具体方法
		Map<String, Map<String, Map<String, Method>>> mappingMap = container.getClassMappingMap();
		// 获得类的全限定名与类实例容器
		Map<String, Object> objectMap = container.getObjectMap();
		// 获得容器中方法与参数列表关系容器
		Map<String, Map<Method, List<RequestMappingParam>>> methodParamMappingMap = container
				.getMethodParamMappingMap();
		// 获取请求路径
		String servletPath = request.getServletPath();
		// 按“/”分隔路径
		String[] servletPathArray = servletPath.split("/");
		// 如果分隔后路径数组长度大于零
		if (servletPathArray.length > 0) {
			// 将第一个赋值给controller类级别的请求路径
			String classPath = servletPathArray[0];
			// 创建方法级别的请求路径
			String methodPath = "";
			// 如果数组第一个路径为空
			if (null == classPath || classPath.equals("")) {
				// 将数组第二个赋值给类路径
				classPath = servletPathArray[1];
				// 重新将“/”字符赋予给类路径
				classPath = "/" + classPath;
				// 将剩余的路径赋值给方法级别的路径
				methodPath = servletPath.substring(classPath.length(), servletPath.length());
			}
			// 通过类级别路径获取Controller类中方法级别请求路径与对应方法存放的容器
			// 该步骤获得Map中第一个String对应类的全限定名，第二个String对应方法级别的请求路径，Method对应对应的请求的具体方法
			Map<String, Map<String, Method>> methodMappingMap = mappingMap.get(classPath);
			// 遍历获取类的全限定名
			Set<String> classNameSet = methodMappingMap.keySet();
			Iterator<String> classNameIt = classNameSet.iterator();
			// 获取类全限定名集合的长度
			int classSize = classNameSet.size();
			// 如果长度大于1
			if (classSize > 1) {
				try {
					// 抛出异常，因为同一个URL只能对应一个类
					throw new Exception("相同的RequestMapping下有多个类");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			// 获取URL对应的类的全限定名
			String className = classNameIt.next();
			// 通过IOC类容器获取该类的实例对象
			// 在这里就是URL对应的controller类
			Object targetObject = objectMap.get(className);
			// 通过URL对应的类全限定名获取该类下所有RquesetMapping的方法
			// 该Map的Key为方法级别的URL，Method为对应的Method
			Map<String, Method> methodMap = methodMappingMap.get(className);
			// 通过controller类的全限定名获取类中方法与参数列表关系容器
			Map<Method, List<RequestMappingParam>> methodParamMap = methodParamMappingMap.get(className);
			// 通过方法级别的URL获取对应的Method
			Method method = methodMap.get(methodPath);
			// 将该方法设置为可访问
			method.setAccessible(true);
			// 获取当前Request参数中所有的参数
			Map<String, String[]> requestParameterMap = request.getParameterMap();
			Set<Entry<String, String[]>> requestParamEntrySet = requestParameterMap.entrySet();
			// 创建Request参数对应的set方法，为了注入到controller的方法做准备
			// Key代表该属性对应的名称，Value代表该属性对应的set方法
			// 比如（private String username） username为Key,setUsername方法为value
			Map<String, String> setMethodNameMap = new HashMap<>();
			// 遍历当前Requset请求中的所有参数
			for (Entry<String, String[]> requesetParamEntry : requestParamEntrySet) {
				// Requset参数名称
				String requsetParamName = requesetParamEntry.getKey();
				// 截取出Requset参数的第一个字母
				String requsetParamFirstStr = requsetParamName.substring(0, 1);
				// 拼凑request参数对应的set方法
				requsetParamName = "set" + requsetParamFirstStr.toUpperCase()
						+ requsetParamName.substring(1, requsetParamName.length());
				// 将生成好的Requset参数对应的Set方法存入List集合
				setMethodNameMap.put(requesetParamEntry.getKey(), requsetParamName);
			}
			// 获取当前执行的Controller类中方法的参数类型
			Class<?>[] methodParamTypes = method.getParameterTypes();
			// 创建URL对应Method的参数列表，为调用该Method做准备
			Object[] args = new Object[methodParamTypes.length];
			// 遍历Controller类中方法的参数类型（参数暂时只能为引用类型的）
			List<RequestMappingParam> paramList = methodParamMap.get(method);
			for (int i = 0; i < args.length; i++) {
				// 获取方法中其中一个参数对应的Class
				RequestMappingParam param = paramList.get(i);
				// 如果方法的参数列表存在某一个参数和方法中的参数名称相同的情况
				if (requestParameterMap.containsKey(param.getParamName())) {
					// 如果方法中参数的JNI基本类型为数组
					if (param.getJniBasicType().startsWith("[")) {
						// TODO 数组类型待开发
					}
					// 如果方法中参数的JNI基本类型为对象
					if (param.getJniBasicType().startsWith("L")) {
						// 如果方法中参数的JNI基本类型为JAVA的Lang包下的包装类
						if (param.getJniBasicType().startsWith("Ljava/lang")) {
							try {
								// 获取其参数为字符串的构造方法，将request中的对应的参数值传到该构造方法中，并通过此构造方法创建对象，赋值给方法的参数列表
								args[i] = param.getParamType().getConstructor(String.class)
										.newInstance(requestParameterMap.get(param.getParamName())[0]);
							} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
									| InvocationTargetException | NoSuchMethodException | SecurityException e) {
								e.printStackTrace();
							}
						} else {
							// 如果方法中参数的JNI类型不为JAVA的Lang包下的包装类，但是却是Object型的，则视request的参数为类对象的其中一个属性
							args[i] = visitObjectFieldParam(param, setMethodNameMap, requestParameterMap);
						}
					}
					// 如果方法中的参数JNI类型为布尔型
					if (param.getJniBasicType().equals("Z")) {
						// String转boolean
						args[i] = Boolean.getBoolean(requestParameterMap.get(param.getParamName())[0]);
					}
					// 如果方法中的参数JNI类型为Byte
					if (param.getJniBasicType().equals("B")) {
						// String转byte
						args[i] = Byte.parseByte(requestParameterMap.get(param.getParamName())[0]);
					}
					// 如果方法中的参数JNI类型为char
					if (param.getJniBasicType().equals("C")) {
						// 只取得该字符串的第一个首字母
						try {
							args[i] = requestParameterMap.get(param.getParamName())[0].charAt(0);
							// 提示
							throw new Exception("您正在将您的参数" + param.getParamName() + "转成char类型，将只处理您的第一个首字符！");
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					// 如果方法中的参数JNI类型为short
					if (param.getJniBasicType().equals("S")) {
						// String转short
						args[i] = Short.valueOf(requestParameterMap.get(param.getParamName())[0]);
					}
					// 如果方法中参数JNI类型为int
					if (param.getJniBasicType().equals("I")) {
						// String转int
						args[i] = Integer.parseInt(requestParameterMap.get(param.getParamName())[0]);
					}
					// 如果方法中参数JNI类型为long
					if (param.getJniBasicType().equals("J")) {
						// String转long
						args[i] = Long.parseLong(requestParameterMap.get(param.getParamName())[0]);
					}
					// 如果方法中参数JNI类型为float
					if (param.getJniBasicType().equals("F")) {
						// String转float
						args[i] = Float.parseFloat(requestParameterMap.get(param.getParamName())[0]);
					}
					// 如果方法中参数JNI类型为double
					if (param.getJniBasicType().equals("D")) {
						// String转double
						args[i] = Double.parseDouble(requestParameterMap.get(param.getParamName())[0]);
					}
				} else {
					// 如果方法的参数列表不存在某一个参数和方法中的参数名称相同的情况
					// 如果方法参数的JNI类型为数组
					if (param.getJniBasicType().startsWith("[")) {
						// 初始化为null
						args[i] = null;
						// 如果方法的参数的JNI类型为Object
					} else if (param.getJniBasicType().startsWith("L")) {
						// 如果方法的参数为java.lang包中的包装类
						if (param.getJniBasicType().startsWith("Ljava/lang")) {
							try {
								// 初始化为null
								args[i] = null;
							} catch (IllegalArgumentException | SecurityException e) {
								e.printStackTrace();
							}
						} else {
							// 如果方法中的参数为非java.lang包下的Object，则视为request的该参数为object中的其中一个属性
							args[i] = visitObjectFieldParam(param, setMethodNameMap, requestParameterMap);
						}
					} else {
						// 如果方法的参数既不是数组型，也不是Object型，则视为基本类型
						try {
							// 提示并抛出异常
							throw new Exception("您的参数" + param.getParamName() + "基本类型由于没有被赋值，所以错误，推荐使用相应的包装类!");
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
			// 执行controller对应方法的前置工作已完毕，执行URL对应的方法
			invoke(method, targetObject, args, request, response);
		}
	}

	@Override
	public ServletContext getServletContext() {
		return super.getServletContext();
	}

	/**
	 * 执行controller对应URL的方法
	 * 
	 * @param method
	 *            对应的method方法
	 * @param targetObject
	 *            对应的controller
	 * @param args
	 *            method方法执行所需要的参数
	 * @param request
	 * @param response
	 */
	private Object invoke(Method method, Object targetObject, Object[] args, HttpServletRequest request,
			HttpServletResponse response) {
		// 执行url对应的method方法，并获得返回值
		Object returnObject = null;
		try {
			// 遍历method方法参数
			for (Object param : args) {
				// 如果参数为model模型
				if (param instanceof Model) {
					// 将model加入request中
					request.setAttribute(DISPATHER_RESULT_MODEL, param);
				}
			}
			// 执行方法并获取返回值
			returnObject = method.invoke(targetObject, args);
			// 执行最后分发工作
			render(returnObject, request, response);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return returnObject;
	}

	/**
	 * 根据方法返回值执行分发操作
	 * 
	 * @param returnObject
	 *            对应的method方法返回值
	 * @param request
	 * @param response
	 */
	private void render(Object returnObject, HttpServletRequest request, HttpServletResponse response) {
		if (returnObject == null) {
			try {
				throw new Exception("URL对应方法没有返回值！");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// 取得本次request请求的model模型
		Model model = (Model) request.getAttribute(DISPATHER_RESULT_MODEL);
		// 如果model不为空
		if (model != null) {
			// 获取model中存入的所有属性
			Map<String, Object> modelAttributes = model.getAttributes();
			// 遍历model所有属性
			Set<Entry<String, Object>> modelEntry = modelAttributes.entrySet();
			Iterator<Entry<String, Object>> modelIt = modelEntry.iterator();
			while (modelIt.hasNext()) {
				Entry<String, Object> modelItem = modelIt.next();
				// 将model中所有属性复制给requset
				request.setAttribute(modelItem.getKey(), modelItem.getValue());
			}
		}
		// 如果controller中方法的返回类型为字符串
		if (returnObject instanceof String) {
			// 强转返回的字符串
			String returnUrl = (String) returnObject;
			// 如果以"/"开头
			if (returnUrl.startsWith("/")) {
				// 取得后面的字符串
				returnUrl = returnUrl.substring(1, returnUrl.length());
			}
			// 拼凑字符串成web路径下的jsp
			returnUrl = "/WEB-INF/jsp/" + returnUrl + ".jsp";
			// 创建Servlet默认的分发器
			RequestDispatcher requestDispatcher = request.getRequestDispatcher(returnUrl);
			// 执行转发
			try {
				requestDispatcher.forward(request, response);
			} catch (ServletException | IOException e) {
				e.printStackTrace();
			}
		}
		// TODO 返回类型为其他的，待开发

	}

	/**
	 * @param param
	 * @param setMethodNameMap
	 * @param requestParameterMap
	 * @return
	 *         <p>
	 *         request参数视为method中参数的属性，则利用此方法进行初始化该method参数
	 *         </p>
	 */
	public Object visitObjectFieldParam(RequestMappingParam param, Map<String, String> setMethodNameMap,
			Map<String, String[]> requestParameterMap) {
		// 获取该方法中参数Class的所有属性（不包括继承的）
		Class<?> methodClass = param.getParamType();
		Field[] fields = methodClass.getDeclaredFields();
		// 创建该参数的实例对象，赋值为NULL
		Object paramInstance = null;
		// 如果controller方法中的参数为接口类型
		if (methodClass.isInterface()) {
			// 如果参数的接口类型为model（自我创建的返回视图模型）类型
			if (Model.class.getName().equals(methodClass.getName())) {
				// 用Model实现类进行创建
				try {
					paramInstance = ModelRealization.class.newInstance();
				} catch (InstantiationException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		} else {
			// 如是其他类型，则创建
			// TODO 参数暂时只能为引用类型的（其他类型待开发）
			try {
				paramInstance = methodClass.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		// 遍历该方法中参数Class的所有属性（不包括继承的）
		for (Field field : fields) {
			// 获取属性名称
			String fieldName = field.getName();
			// 获取属性对应的类型Class
			Class<?> fieldType = field.getType();
			// 创建URL对应方法中参数的Class的方法
			Method paramMethod = null;
			try {
				// 通过121行创建的Set方法集合获取该
				// Key代表该属性对应的名称，Value代表该属性对应的set方法
				// 比如（private String username）
				// username为Key,setUsername方法为value
				// 这里fieldName为username,获取的methodName 为
				// setUsername
				String methodName = setMethodNameMap.get(fieldName);
				// 如果set方法为空，则中断当前循环，进入下一次循环
				if (methodName == null) {
					continue;
				}
				// 如果set方法存在，则获取该参数class中的set方法
				paramMethod = methodClass.getMethod(methodName, new Class[] { fieldType });
				// 如果该方法存在
				if (paramMethod != null) {
					// 如果URL调用的方法的参数的class的参数与request请求的名称相同
					if (requestParameterMap.containsKey(fieldName)) {
						// 创建参数列表中对应的实例对象
						Object[] paramObject = new Object[] { requestParameterMap.get(fieldName)[0] };
						try {
							// 调用URL对应方法的参数的set方法，进行赋值
							paramMethod.invoke(paramInstance, paramObject);
						} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
							e.printStackTrace();
						}
					}
				}
			} catch (NoSuchMethodException | SecurityException e) {
				// 如果不存在该方法则继续寻找
				continue;
			}
		}
		// 将参数实例对象赋值加入到参数列表
		return paramInstance;
	}

}
