/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dborisenko.math.optimization.problems.descriptors.fields;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 *
 * @author Denis
 */
public class DefaultFieldDescriptor implements FieldDescriptor {

    private final Field field;

    public DefaultFieldDescriptor(Field field) {
        this.field = field;
    }

    public void setAccessible(boolean flag) throws SecurityException {
        field.setAccessible(flag);
    }

    public Object get(Object obj)
        throws IllegalArgumentException, IllegalAccessException {
        return field.get(obj);
    }

    public Annotation getAnnotation(Class annotationClass) {
        return field.getAnnotation(annotationClass);
    }

    public int getModifiers() {
	return field.getModifiers();
    }

    public String getName() {
        return field.getName();
    }

    public Class getDeclaringClass() {
	return field.getDeclaringClass();
    }

}
