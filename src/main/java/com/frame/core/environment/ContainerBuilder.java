package com.frame.core.environment;

/**
 * @author god 此类为容器构造器
 */
public class ContainerBuilder {
	// 利用ThreadLocal存储容器，实现容器的线程安全
	private final static ThreadLocal<Container> containerThread = new ThreadLocal<>();
	// 容器类
	private static Container container;

	/**
	 * 私有化构造方法，防止自由创建
	 */
	private ContainerBuilder() {
	};

	public final static Container createContainer() {
		// 如果容器为NULL
		if (null == container) {
			// 创建容器
			container = new Container();
			// 将该容器存入ThreadLocal中
			containerThread.set(container);
		}
		return containerThread.get();
	}
}
