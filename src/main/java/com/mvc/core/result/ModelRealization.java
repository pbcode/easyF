package com.mvc.core.result;

import java.util.Map;
import java.util.Set;

/**
 * @author god
 *         <p>
 *         视图实现类
 *         </p>
 */
public class ModelRealization implements Model {
	// 创建modelMap,用于存放视图元素的map，继承于linkedHashMap
	private final ModelMap modelMap = new ModelMap();

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mvc.core.result.Model#addAttribute(java.lang.String,
	 * java.lang.Object) 用于存放属性
	 */
	public Model addAttribute(String key, Object val) {
		modelMap.put(key, val);
		return this;
	}

	@Override
	public Object getAttribute(String key) {
		return modelMap.get(key);
	}

	@Override
	public Map<String, Object> getAttributes() {
		return modelMap;
	}

	@Override
	public Set<String> getKeys() {
		return modelMap.keySet();
	}
}
