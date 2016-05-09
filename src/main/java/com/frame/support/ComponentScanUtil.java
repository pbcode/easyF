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
 *         根据package路径获取信息工具类
 *         </p>
 */
public class ComponentScanUtil {

	/**
	 * @param packageNameList
	 * @return
	 *         <p>
	 *         根据package内容获取类CLASS
	 *         </p>
	 */
	public static Map<String, Class<?>> getClasses(List<String> packageNameList) {
		try {
			// 创建CLASS容器
			Map<String, Class<?>> allClassMap = new LinkedHashMap<>();
			// 遍历package路径
			for (String packageName : packageNameList) {
				// 将包路径末尾添加"/"
				String packageDirName = packageName + "/";
				// 根据包获取包路径
				Enumeration<URL> urls = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
				// 如果有更多的子包
				while (urls.hasMoreElements()) {
					// 取得下面的子包
					URL url = urls.nextElement();
					// 如果类型是文件
					if (url.getProtocol().equals("file")) {
						// 取得这个文件路径
						String filePath = url.getFile();
						// 调用文件查找方法进行查找，并通过递归方式循环取得类容器
						allClassMap = findClassByFile(filePath, packageName, allClassMap);
					}
				}
			}
			// 返回类容器
			return allClassMap;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @param filePath
	 *            文件路径
	 * @param packageDirName
	 *            包路径
	 * @param classList
	 *            类CLASS容器
	 * @return
	 */
	private static Map<String, Class<?>> findClassByFile(String filePath, String packageDirName,
			Map<String, Class<?>> allClassMap) {
		// 根据文件路径创建文件
		File dir = new File(filePath);
		// 如果文件不存在或者不是文件夹形式
		if (!dir.exists() || !dir.isDirectory()) {
			// 返回NULL
			return null;
		}
		// 如果该文件是文件夹，则查找文件夹下所有文件，过滤条件为是文件夹或者是以.class结尾的文件
		File[] files = dir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File file, String name) {
				return file.isDirectory() || file.getName().endsWith(".class");
			}
		});
		// 遍历这些文件
		for (File file : files) {
			// 如果文件是文件夹
			if (file.isDirectory()) {
				// 递归调用继续进行查找
				findClassByFile(file.getAbsolutePath(), packageDirName + "." + file.getName(), allClassMap);
			} else {
				// 如果是文件，则获取该文件名截取以后6位，即class文件的文件名
				String className = file.getName().substring(0, file.getName().length() - 6);
				try {
					// 创建该class，并加入到class容器中
					allClassMap.put(packageDirName + '.' + className, Class.forName(packageDirName + '.' + className));
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		// 返回class容器
		return allClassMap;
	}

}
