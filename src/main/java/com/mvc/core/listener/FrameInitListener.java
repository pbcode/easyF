package com.mvc.core.listener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.apache.catalina.Context;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.loader.WebappClassLoader;

import com.frame.core.ioc.AnnotationIocBuilder;
import com.mvc.core.mapping.RequestMappingWapper;

/**
 * @author god
 *         <p>
 *         框架初始化监听器，用于创建容器
 *         </p>
 */
@WebListener
public class FrameInitListener implements ServletContextListener {

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {

	}

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		// 初始化容器
		initContainer(getPackageNameList(servletContextEvent));
	}

	/**
	 * @param packageNameList
	 *            <p>
	 *            通过包路径初始化容器
	 *            </p>
	 */
	private void initContainer(List<String> packageNameList) {
		// 调用IOC构造器来初始化容器
		AnnotationIocBuilder.initContainer(packageNameList);
		// 调用URL与类查找的功能类进行初始化RquestMapping容器，参数为当前容器
		RequestMappingWapper.initClassMappingMap(AnnotationIocBuilder.getContainer());
	}

	/**
	 * @param servletContextEvent
	 * @return
	 * 		<p>
	 *         通过servletContext获取当前Web端的所有包路径，实现“零配置包功能”
	 *         </p>
	 */
	private List<String> getPackageNameList(ServletContextEvent servletContextEvent) {
		// 获取servletContext
		ServletContext ctx = servletContextEvent.getServletContext();
		// 获取servletContext类的类加载器，也就是Tomcat的当前类加载器
		WebappClassLoader classLoader = (WebappClassLoader) ctx.getClassLoader();
		// 通过类加载器获取WebResourceRoot
		WebResourceRoot root = classLoader.getResources();
		// 获取当前WebResourceRoot的上下文
		Context c = root.getContext();
		// 创建存放package路径的容器
		List<String> packageNameList = new ArrayList<>();
		// 根据DocBase路径 拼凑出该web的class文件夹
		File file = new File(c.getDocBase() + "/WEB-INF/classes/");
		// 如果该文件是文件夹
		if (file.isDirectory()) {
			// 遍历该文件夹下所有文件
			File[] files = file.listFiles();
			for (File packageDir : files) {
				// 如果该文件是文件夹，则说明是包
				if (packageDir.isDirectory()) {
					// 获取该文件夹的名称
					String packageName = packageDir.getName();
					// 放入容器
					packageNameList.add(packageName);
				}
			}
		}
		// 返回容器
		return packageNameList;
	}
}
