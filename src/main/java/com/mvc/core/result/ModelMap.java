package com.mvc.core.result;

import java.util.LinkedHashMap;

/**
 * @author god
 *         <p>
 *         存放视图元素的map，继承于linkedHashMap
 *         </p>
 */
@SuppressWarnings("serial")
public class ModelMap extends LinkedHashMap<String, Object> {

	/**
	 * @param name
	 * @param value
	 * @return
	 *         <p>
	 *         添加属性
	 *         </p>
	 */
	public ModelMap addAttribute(String name, Object value) {
		put(name, value);
		return this;
	}
}
