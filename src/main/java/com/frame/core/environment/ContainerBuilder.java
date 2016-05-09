package com.frame.core.environment;

/**
 * @author god ����Ϊ����������
 */
public class ContainerBuilder {
	// ����ThreadLocal�洢������ʵ���������̰߳�ȫ
	private final static ThreadLocal<Container> containerThread = new ThreadLocal<>();
	// ������
	private static Container container;

	/**
	 * ˽�л����췽������ֹ���ɴ���
	 */
	private ContainerBuilder() {
	};

	public final static Container createContainer() {
		// �������ΪNULL
		if (null == container) {
			// ��������
			container = new Container();
			// ������������ThreadLocal��
			containerThread.set(container);
		}
		return containerThread.get();
	}
}
