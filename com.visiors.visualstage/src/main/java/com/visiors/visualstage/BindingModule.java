package com.visiors.visualstage;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Binder;
import com.google.inject.Module;

public class BindingModule implements Module {

    private final Logger logger = LoggerFactory.getLogger(BindingModule.class);

    @Override
    public void configure(Binder binder) {

        doBindings(binder);
    }

    private void doBindings(Binder binder) {

        Method[] methods = this.getClass().getMethods();
        for (Method method : methods) {
            try {
                if (method.getName().startsWith("bind")) {
                    method.invoke(this, binder);
                }
            } catch (Exception e) {
                logger.warn("Trying to use method " + method.toGenericString() + " for configuration failed", e);
            }
        }
    }

}
