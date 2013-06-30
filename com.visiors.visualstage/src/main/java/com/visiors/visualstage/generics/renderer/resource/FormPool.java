package com.visiors.visualstage.generics.renderer.resource;

import java.util.HashMap;
import java.util.Map;

public class FormPool {
	// private static final SoftThreadLocal<HashMap> pool = new
	// SoftThreadLocal<HashMap>() {
	// @Override
	// protected synchronized HashMap initialValue() {
	// return new HashMap();
	// }
	// };

	static Map<String, Form> poolLocal = new HashMap<String, Form>();

	public FormPool() {
	}

	public static final void pool(Form form) {
		// final HashMap<String, String> poolLocal = pool.get();
		poolLocal.put(form.getName(), form);
	}

	public static final void remove(String key) {
		// final HashMap<String, String> poolLocal = pool.get();
		poolLocal.remove(key);
	}

	public static final boolean containsKey(String key) {
		// final HashMap<String, String> poolLocal = pool.get();
		return poolLocal.containsKey(key);
	}

	public static final Form get(String key) {
		// final HashMap<String, String> poolLocal = pool.get();
		return poolLocal.get(key);
	}

}
