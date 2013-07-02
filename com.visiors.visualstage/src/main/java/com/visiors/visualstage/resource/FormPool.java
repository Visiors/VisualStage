package com.visiors.visualstage.resource;

import java.util.HashMap;
import java.util.Map;

import com.visiors.visualstage.form.Form;

public class FormPool {
    // private static final SoftThreadLocal<HashMap> pool = new SoftThreadLocal<HashMap>() {
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
        FormPool.poolLocal.put(form.getName(), form);
    }

    public static final void remove(String key) {

        // final HashMap<String, String> poolLocal = pool.get();
        FormPool.poolLocal.remove(key);
    }

    public static final boolean containsKey(String key) {

        // final HashMap<String, String> poolLocal = pool.get();
        return FormPool.poolLocal.containsKey(key);
    }

    public static final Form get(String key) {

        // final HashMap<String, String> poolLocal = pool.get();
        return FormPool.poolLocal.get(key);
    }

}
