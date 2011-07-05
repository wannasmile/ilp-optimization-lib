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
public interface FieldDescriptor {

    void setAccessible(boolean flag) throws SecurityException;
    Object get(Object obj)
        throws IllegalArgumentException, IllegalAccessException;
    Annotation getAnnotation(Class annotationClass);
    int getModifiers();
    String getName();
    Class getDeclaringClass();
    
}
