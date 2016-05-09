package com.mvc.core.result;

import java.util.Map;
import java.util.Set;

public interface Model {

	public Model addAttribute(String key, Object val);

	public Object getAttribute(String key);

	public Map<String, Object> getAttributes();

	public Set<String> getKeys();
}
