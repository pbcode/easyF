package com.frame.core.ioc;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.frame.core.environment.Container;
import com.frame.core.environment.ContainerBuilder;
import com.frame.support.ComponentScanUtil;
import com.mvc.support.annotation.Autowired;
import com.mvc.support.annotation.Screw;

/**
 * @author god
 *         <p>
 *         IOC实现类，实现控制反转与依赖注入
 *         </p>
 */
public class AnnotationIocBuilder {

	// 通过容器构造器创建线程安全的容器
	private static final Container container = ContainerBuilder.createContainer();
	// 创建IOC容器，存放所有添加了注解Screw的类
	private static final Map<String, Object> objectMap = new HashMap<String, Object>();

	/**
	 * 初始化控制反转
	 * 
	 * @param packageNameList
	 */
	private static void initIoc(List<String> packageNameList) {
		// 通过组建扫描工具类按照package路径进行class查找
		Map<String, Class<?>> allClassMap = ComponentScanUtil.getClasses(packageNameList);
		// 创建注解CLASS
		Class<Screw> annotationClass = Screw.class;
		// 迭代查找后的class们
		Iterator<String> it = allClassMap.keySet().iterator();
		while (it.hasNext()) {
			Class<?> clazz = allClassMap.get(it.next());
			// 如果某类使用了Screw注解
			if (clazz.isAnnotationPresent(annotationClass)) {
				try {
					// 将该类实例化，并存入IOC容器中
					objectMap.put(clazz.getName(), clazz.newInstance());
				} catch (InstantiationException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
		// 将ioc的所有对象放入容器中
		container.setObjectMap(objectMap);
	}

	/**
	 * 初始化依赖注入
	 */
	private static void initInject() {
		// 迭代这些Class
		Iterator<String> it = objectMap.keySet().iterator();
		while (it.hasNext()) {
			Class<?> clazz = objectMap.get(it.next()).getClass();
			// 获取该类的所有成员变量（不包括继承）
			Field[] fields = clazz.getDeclaredFields();
			// 遍历这些成员变量
			for (Field field : fields) {
				// 如果IOC容器不为空
				Object fatherObject = objectMap.get(clazz.getName());
				if (!objectMap.isEmpty()) {
					// 如果该成员变量使用了Autowired注解
					if (field.isAnnotationPresent(Autowired.class)) {
						// 获取该成员变量的类型
						Class<?> fileldTypeClass = field.getType();
						// 如果该成员变量为接口类型
						if (fileldTypeClass.isInterface()) {
							// 重现遍历IOC容器
							Set<String> objectSet = objectMap.keySet();
							Iterator<String> objectIt = objectSet.iterator();
							while (objectIt.hasNext()) {
								// 迭代获得IOC容器的中一个实例化对象
								Object o = objectMap.get(objectIt.next());
								// 如果这个实例化对象的接口是该成员变量类型
								if (fileldTypeClass.isInstance(o)) {
									// 则获取该成员变量所在的Class
									// 将该成员变量设置可访问
									field.setAccessible(true);
									try {
										// 将这个实例变量设置到该成员变量中
										field.set(fatherObject, o);
									} catch (IllegalArgumentException | IllegalAccessException e) {
										e.printStackTrace();
									}
									// 既然已经将这个字段赋值则跳出循环
									break;
								}
							}
						} else {
							// TODO 其他类型待开发
							// 如果该成员变量不是接口类型，从IOC容器中直接拿出该对象
							Object fieldObject = objectMap.get(fileldTypeClass.getName());
							try {
								// 如果注入的类不存在在报错
								if (fieldObject == null) {
									throw new Exception("被注入的类不存在！");
								}
								// 设置该成员变量为可访问
								field.setAccessible(true);
								// 将实例对象注入
								field.set(fatherObject, fieldObject);
							} catch (IllegalAccessException e) {
								e.printStackTrace();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						// 将依赖注入好的类实例对象重新放入IOC容器中
						objectMap.put(clazz.getName(), fatherObject);
					}
				}
			}
		}
	}

	/**
	 * @param packageNameList
	 *            初始化容器
	 */
	public static void initContainer(List<String> packageNameList) {
		// 初始化IOC
		initIoc(packageNameList);
		// 初始化注入
		initInject();
	}

	/**
	 * 获取当前容器
	 */
	public static Container getContainer() {
		return container;
	}

}
