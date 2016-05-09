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
 *         ��ܳ�ʼ�������������ڴ�������
 *         </p>
 */
@WebListener
public class FrameInitListener implements ServletContextListener {

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {

	}

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		// ��ʼ������
		initContainer(getPackageNameList(servletContextEvent));
	}

	/**
	 * @param packageNameList
	 *            <p>
	 *            ͨ����·����ʼ������
	 *            </p>
	 */
	private void initContainer(List<String> packageNameList) {
		// ����IOC����������ʼ������
		AnnotationIocBuilder.initContainer(packageNameList);
		// ����URL������ҵĹ�������г�ʼ��RquestMapping����������Ϊ��ǰ����
		RequestMappingWapper.initClassMappingMap(AnnotationIocBuilder.getContainer());
	}

	/**
	 * @param servletContextEvent
	 * @return
	 * 		<p>
	 *         ͨ��servletContext��ȡ��ǰWeb�˵����а�·����ʵ�֡������ð����ܡ�
	 *         </p>
	 */
	private List<String> getPackageNameList(ServletContextEvent servletContextEvent) {
		// ��ȡservletContext
		ServletContext ctx = servletContextEvent.getServletContext();
		// ��ȡservletContext������������Ҳ����Tomcat�ĵ�ǰ�������
		WebappClassLoader classLoader = (WebappClassLoader) ctx.getClassLoader();
		// ͨ�����������ȡWebResourceRoot
		WebResourceRoot root = classLoader.getResources();
		// ��ȡ��ǰWebResourceRoot��������
		Context c = root.getContext();
		// �������package·��������
		List<String> packageNameList = new ArrayList<>();
		// ����DocBase·�� ƴ�ճ���web��class�ļ���
		File file = new File(c.getDocBase() + "/WEB-INF/classes/");
		// ������ļ����ļ���
		if (file.isDirectory()) {
			// �������ļ����������ļ�
			File[] files = file.listFiles();
			for (File packageDir : files) {
				// ������ļ����ļ��У���˵���ǰ�
				if (packageDir.isDirectory()) {
					// ��ȡ���ļ��е�����
					String packageName = packageDir.getName();
					// ��������
					packageNameList.add(packageName);
				}
			}
		}
		// ��������
		return packageNameList;
	}
}
