/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dborisenko.math.optimization.problems.descriptors.fields;

import com.dborisenko.math.optimization.Solver;
import com.dborisenko.math.optimization.problems.descriptors.ClassDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Denis
 */
public class OptimizationTimeFieldDescriptor implements FieldDescriptor {

    public static final String NAME = "time";
    private static final String START_TIME_FIELD_NAME = "startTime";
    private static final String STOP_TIME_FIELD_NAME = "stopTime";

    public OptimizationTimeFieldDescriptor() {
    }

    public void setAccessible(boolean flag) throws SecurityException {
    }

    public Object get(Object obj) throws IllegalArgumentException, IllegalAccessException {
        ClassDescriptor c = new ClassDescriptor(obj.getClass());
        try {
            FieldDescriptor startTimeField = c.getField(START_TIME_FIELD_NAME);
            FieldDescriptor stopTimeField = c.getField(STOP_TIME_FIELD_NAME);

            startTimeField.setAccessible(true);
            stopTimeField.setAccessible(true);
            
            Date startTime = (Date) startTimeField.get(obj);
            Date stopTime = (Date) stopTimeField.get(obj);
            if (startTime == null) {
                startTime = new Date();
            }
            if (stopTime == null) {
                stopTime = new Date();
            }
            
            return stopTime.getTime() - startTime.getTime();
        } catch (NoSuchFieldException ex) {
            Logger.getLogger(OptimizationTimeFieldDescriptor.class.getName()).log(Level.SEVERE, null, ex);
            throw new IllegalArgumentException();
        } catch (SecurityException ex) {
            Logger.getLogger(OptimizationTimeFieldDescriptor.class.getName()).log(Level.SEVERE, null, ex);
            throw new IllegalArgumentException();
        }
    }

    public Annotation getAnnotation(Class annotationClass) {
        return null;
    }

    public int getModifiers() {
        return 0;
    }

    public String getName() {
        return NAME;
    }

    public Class getDeclaringClass() {
        return Solver.class;
    }
}
