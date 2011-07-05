/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dborisenko.math.utils;

import java.lang.reflect.Array;
import java.util.Arrays;
import org.apache.commons.math.Field;
import org.apache.commons.math.FieldElement;
import org.apache.commons.math.util.MathUtils;

/**
 *
 * @author Denis
 */
public abstract class FieldHelper<ValueField extends FieldElement<ValueField>> {
    protected Field<ValueField> field;

    public FieldHelper(Field<ValueField> field) {
        this.field = field;
    }

    public Field<ValueField> getField() {
        return field;
    }

    public ValueField getZero() {
        return field.getZero();
    }

    public ValueField getOne() {
        return field.getOne();
    }

    public ValueField getMinusOne() {
        return getZero().subtract(getOne());
    }

    public ValueField getNegative(ValueField value) {
        return value.multiply(getMinusOne());
    }

    public ValueField getFractionalPart(ValueField value) {
        return value.subtract(getIntegerPart(value));
    }

    public ValueField getIntegerPart(ValueField value) {
        return fromDouble(IntegerUtils.getIntegerPart(toDouble(value)));
    }

    public abstract ValueField fromDouble(double value);
    public abstract double toDouble(ValueField value);

    public ValueField[] buildArray(int length) {
        return buildArray(field, length);
    }
    
    protected static <T extends FieldElement<T>> T[] buildArray(final Field<T> field,
                                                                final int length) {
        T[] array = (T[]) Array.newInstance(field.getZero().getClass(), length);
        Arrays.fill(array, field.getZero());
        return array;
    }

    public boolean equals(ValueField x, ValueField y, double eps) {
        return MathUtils.equals(toDouble(x), toDouble(y), eps);
    }
    public boolean equals(ValueField x, double y, double eps) {
        return MathUtils.equals(toDouble(x), y, eps);
    }
    public boolean equals(double x, ValueField y, double eps) {
        return MathUtils.equals(x, toDouble(y), eps);
    }
    public boolean equals(double x, double y, double eps) {
        return MathUtils.equals(x, y, eps);
    }

    public int compareTo(ValueField x, ValueField y, double eps) {
        return MathUtils.compareTo(toDouble(x), toDouble(y), eps);
    }
    public int compareTo(ValueField x, double y, double eps) {
        return MathUtils.compareTo(toDouble(x), y, eps);
    }
    public int compareTo(double x, ValueField y, double eps) {
        return MathUtils.compareTo(x, toDouble(y), eps);
    }
    public int compareTo(double x, double y, double eps) {
        return MathUtils.compareTo(x, y, eps);
    }
}
