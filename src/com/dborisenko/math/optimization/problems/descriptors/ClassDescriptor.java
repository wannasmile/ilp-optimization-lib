package com.dborisenko.math.optimization.problems.descriptors;

import com.dborisenko.math.optimization.problems.descriptors.fields.DefaultFieldDescriptor;
import com.dborisenko.math.optimization.problems.descriptors.fields.FieldDescriptor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Denis
 */
public class ClassDescriptor<ClassType> {

    private Class instanceClass;

    public ClassDescriptor(Class instanceClass) {
        this.instanceClass = instanceClass;
    }

    public ClassType newInstance() 
            throws InstantiationException, IllegalAccessException {
        return (ClassType)instanceClass.newInstance();
    }

    public String getName() {
        return instanceClass.getName();
    }

    public Collection<FieldDescriptor> getAllFields() {
        return getAllFields(null);
    }

    protected List<FieldDescriptor> getAllFields(String[] ignoreFields) {
        List<FieldDescriptor> fields = new ArrayList<FieldDescriptor>();
        Class currentClass = instanceClass;
        List<String> ignores = (ignoreFields != null ? Arrays.asList(ignoreFields) : null);

        do {
            Field[] fs = currentClass.getDeclaredFields();
            for (int i = 0; i < fs.length; i++) {
                if (ignores != null && ignores.contains(fs[i].getName())) {
                    continue;
                }
                fields.add(new DefaultFieldDescriptor(fs[i]));
            }
            currentClass = currentClass.getSuperclass();
        } while (currentClass != null && !currentClass.equals(Object.class));

        return fields;
    }

    public FieldDescriptor getField(String name)
            throws NoSuchFieldException {
        return getDeclaratedField(name, instanceClass);
    }

    private FieldDescriptor getDeclaratedField(String name, Class currentClass)
            throws NoSuchFieldException {
        if (currentClass == null || currentClass.equals(Object.class)) {
            throw new NoSuchFieldException("Field " + name + " doesn't exist");
        }
        try {
            return new DefaultFieldDescriptor(currentClass.getDeclaredField(name));
        } catch (NoSuchFieldException ex) {
            return getDeclaratedField(name, currentClass.getSuperclass());
        }
    }
}
