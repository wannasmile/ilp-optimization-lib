/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dborisenko.math.optimization.problems.descriptors;

import com.dborisenko.math.optimization.problems.descriptors.fields.FieldDescriptor;
import com.dborisenko.math.optimization.problems.descriptors.fields.OptimizationTimeFieldDescriptor;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author Denis
 */
public class OptimizerDescriptor extends ClassDescriptor {

    private static final String[] IGNORE_FIELDS = new String[] {
        "startTime", "stopTime", "epsilon"
    };

    public OptimizerDescriptor(Class instanceClass) {
        super(instanceClass);
    }

    @Override
    public Collection<FieldDescriptor> getAllFields() {
        List<FieldDescriptor> fields = getAllFields(IGNORE_FIELDS);
        fields.add(new OptimizationTimeFieldDescriptor());
        return fields;
    }

    @Override
    public FieldDescriptor getField(String name)
            throws NoSuchFieldException {
        if (name.equals(OptimizationTimeFieldDescriptor.NAME)) {
            return new OptimizationTimeFieldDescriptor();
        }
        return super.getField(name);
    }

}
