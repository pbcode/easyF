package com.frame.support;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author god
 *         <p>
 *         ����package·����ȡ��Ϣ������
 *         </p>
 */
public class ComponentScanUtil {

	/**
	 * @param packageNameList
	 * @return
	 *         <p>
	 *         ����package���ݻ�ȡ��CLASS
	 *         </p>
	 */
	public static Map<String, Class<?>> getClasses(List<String> packageNameList) {
		try {
			// ����CLASS����
			Map<String, Class<?>> allClassMap = new LinkedHashMap<>();
			// ����package·��
			for (String packageName : packageNameList) {
				// ����·��ĩβ���"/"
				String packageDirName = packageName + "/";
				// ���ݰ���ȡ��·��
				Enumeration<URL> urls = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
				// ����и�����Ӱ�
				while (urls.hasMoreElements()) {
					// ȡ��������Ӱ�
					URL url = urls.nextElement();
					// ����������ļ�
					if (url.getProtocol().equals("file")) {
						// ȡ������ļ�·��
						String filePath = url.getFile();
						// �����ļ����ҷ������в��ң���ͨ���ݹ鷽ʽѭ��ȡ��������
						allClassMap = findClassByFile(filePath, packageName, allClassMap);
					}
				}
			}
			// ����������
			return allClassMap;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @param filePath
	 *            �ļ�·��
	 * @param packageDirName
	 *            ��·��
	 * @param classList
	 *            ��CLASS����
	 * @return
	 */
	private static Map<String, Class<?>> findClassByFile(String filePath, String packageDirName,
			Map<String, Class<?>> allClassMap) {
		// �����ļ�·�������ļ�
		File dir = new File(filePath);
		// ����ļ������ڻ��߲����ļ�����ʽ
		if (!dir.exists() || !dir.isDirectory()) {
			// ����NULL
			return null;
		}
		// ������ļ����ļ��У�������ļ����������ļ�����������Ϊ���ļ��л�������.class��β���ļ�
		File[] files = dir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File file, String name) {
				return file.isDirectory() || file.getName().endsWith(".class");
			}
		});
		// ������Щ�ļ�
		for (File file : files) {
			// ����ļ����ļ���
			if (file.isDirectory()) {
				// �ݹ���ü������в���
				findClassByFile(file.getAbsolutePath(), packageDirName + "." + file.getName(), allClassMap);
			} else {
				// ������ļ������ȡ���ļ�����ȡ�Ժ�6λ����class�ļ����ļ���
				String className = file.getName().substring(0, file.getName().length() - 6);
				try {
					// ������class�������뵽class������
					allClassMap.put(packageDirName + '.' + className, Class.forName(packageDirName + '.' + className));
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		// ����class����
		return allClassMap;
	}

}
