package com.visiors.visualstage.form;

import com.visiors.visualstage.property.PropertyList;

public class FormBuilder {

    public static DefaultForm createFormTemplate(PropertyList properties) {

        DefaultForm form = null;
        try {
            form = new DefaultForm(properties, null);
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to create form based on this description: " + properties);
        }
        return form;
    }

}
