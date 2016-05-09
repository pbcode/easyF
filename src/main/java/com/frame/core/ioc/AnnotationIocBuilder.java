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
 *         IOCʵ���࣬ʵ�ֿ��Ʒ�ת������ע��
 *         </p>
 */
public class AnnotationIocBuilder {

	// ͨ�����������������̰߳�ȫ������
	private static final Container container = ContainerBuilder.createContainer();
	// ����IOC������������������ע��Screw����
	private static final Map<String, Object> objectMap = new HashMap<String, Object>();

	/**
	 * ��ʼ�����Ʒ�ת
	 * 
	 * @param packageNameList
	 */
	private static void initIoc(List<String> packageNameList) {
		// ͨ���齨ɨ�蹤���ఴ��package·������class����
		Map<String, Class<?>> allClassMap = ComponentScanUtil.getClasses(packageNameList);
		// ����ע��CLASS
		Class<Screw> annotationClass = Screw.class;
		// �������Һ��class��
		Iterator<String> it = allClassMap.keySet().iterator();
		while (it.hasNext()) {
			Class<?> clazz = allClassMap.get(it.next());
			// ���ĳ��ʹ����Screwע��
			if (clazz.isAnnotationPresent(annotationClass)) {
				try {
					// ������ʵ������������IOC������
					objectMap.put(clazz.getName(), clazz.newInstance());
				} catch (InstantiationException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
		// ��ioc�����ж������������
		container.setObjectMap(objectMap);
	}

	/**
	 * ��ʼ������ע��
	 */
	private static void initInject() {
		// ������ЩClass
		Iterator<String> it = objectMap.keySet().iterator();
		while (it.hasNext()) {
			Class<?> clazz = objectMap.get(it.next()).getClass();
			// ��ȡ��������г�Ա�������������̳У�
			Field[] fields = clazz.getDeclaredFields();
			// ������Щ��Ա����
			for (Field field : fields) {
				// ���IOC������Ϊ��
				Object fatherObject = objectMap.get(clazz.getName());
				if (!objectMap.isEmpty()) {
					// ����ó�Ա����ʹ����Autowiredע��
					if (field.isAnnotationPresent(Autowired.class)) {
						// ��ȡ�ó�Ա����������
						Class<?> fileldTypeClass = field.getType();
						// ����ó�Ա����Ϊ�ӿ�����
						if (fileldTypeClass.isInterface()) {
							// ���ֱ���IOC����
							Set<String> objectSet = objectMap.keySet();
							Iterator<String> objectIt = objectSet.iterator();
							while (objectIt.hasNext()) {
								// �������IOC��������һ��ʵ��������
								Object o = objectMap.get(objectIt.next());
								// ������ʵ��������Ľӿ��Ǹó�Ա��������
								if (fileldTypeClass.isInstance(o)) {
									// ���ȡ�ó�Ա�������ڵ�Class
									// ���ó�Ա�������ÿɷ���
									field.setAccessible(true);
									try {
										// �����ʵ���������õ��ó�Ա������
										field.set(fatherObject, o);
									} catch (IllegalArgumentException | IllegalAccessException e) {
										e.printStackTrace();
									}
									// ��Ȼ�Ѿ�������ֶθ�ֵ������ѭ��
									break;
								}
							}
						} else {
							// TODO �������ʹ�����
							// ����ó�Ա�������ǽӿ����ͣ���IOC������ֱ���ó��ö���
							Object fieldObject = objectMap.get(fileldTypeClass.getName());
							try {
								// ���ע����಻�����ڱ���
								if (fieldObject == null) {
									throw new Exception("��ע����಻���ڣ�");
								}
								// ���øó�Ա����Ϊ�ɷ���
								field.setAccessible(true);
								// ��ʵ������ע��
								field.set(fatherObject, fieldObject);
							} catch (IllegalAccessException e) {
								e.printStackTrace();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						// ������ע��õ���ʵ���������·���IOC������
						objectMap.put(clazz.getName(), fatherObject);
					}
				}
			}
		}
	}

	/**
	 * @param packageNameList
	 *            ��ʼ������
	 */
	public static void initContainer(List<String> packageNameList) {
		// ��ʼ��IOC
		initIoc(packageNameList);
		// ��ʼ��ע��
		initInject();
	}

	/**
	 * ��ȡ��ǰ����
	 */
	public static Container getContainer() {
		return container;
	}

}
