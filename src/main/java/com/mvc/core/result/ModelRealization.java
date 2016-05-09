package com.mvc.core.result;

import java.util.Map;
import java.util.Set;

/**
 * @author god
 *         <p>
 *         ��ͼʵ����
 *         </p>
 */
public class ModelRealization implements Model {
	// ����modelMap,���ڴ����ͼԪ�ص�map���̳���linkedHashMap
	private final ModelMap modelMap = new ModelMap();

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mvc.core.result.Model#addAttribute(java.lang.String,
	 * java.lang.Object) ���ڴ������
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
