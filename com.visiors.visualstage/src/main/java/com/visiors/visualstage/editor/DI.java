package com.visiors.visualstage.editor;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

public class DI {

	private static Injector injector;

	public static Injector init(Module module) {

		injector = Guice.createInjector(module);
		return injector;
	}

	public static Injector getInjector() {

		return injector;
	}

	public static void injectMembers(Object object) {

		DI.getInjector().injectMembers(object);
	}

	public static <T> T getInstance(Class<T> type) {

		return DI.getInjector().getInstance(type);
	}
}
