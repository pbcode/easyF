package com.mvc.core.result;

import java.util.LinkedHashMap;

/**
 * @author god
 *         <p>
 *         �����ͼԪ�ص�map���̳���linkedHashMap
 *         </p>
 */
@SuppressWarnings("serial")
public class ModelMap extends LinkedHashMap<String, Object> {

	/**
	 * @param name
	 * @param value
	 * @return
	 *         <p>
	 *         �������
	 *         </p>
	 */
	public ModelMap addAttribute(String name, Object value) {
		put(name, value);
		return this;
	}
}
