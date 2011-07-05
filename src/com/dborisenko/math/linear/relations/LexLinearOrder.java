package com.dborisenko.math.linear.relations;

import com.dborisenko.math.linear.VectorUtils;
import com.dborisenko.math.utils.FieldHelper;
import java.util.Iterator;
import org.apache.commons.math.FieldElement;
import org.apache.commons.math.linear.FieldVector;
import org.apache.commons.math.linear.RealVector;
import org.apache.commons.math.util.MathUtils;

/**
 *
 * @author Denis Borisenko
 */
public class LexLinearOrder<ValueField extends FieldElement<ValueField>> {

    private FieldHelper<ValueField> fieldHelper;

    /**
     * Compares two numbers given some amount of allowed error.
     *
     * @param x the first number
     * @param y the second number
     * @param eps the amount of error to allow when checking for equality
     * @return <ul><li>0 if  {@link #equals(double, double, double) equals(x, y, eps)}</li>
     *       <li>&lt; 0 if !{@link #equals(double, double, double) equals(x, y, eps)} &amp;&amp; x &lt; y</li>
     *       <li>> 0 if !{@link #equals(double, double, double) equals(x, y, eps)} &amp;&amp; x > y</li></ul>
     */
    public static int compareWithZero(RealVector v, double eps) {
        Iterator<RealVector.Entry> iter = v.iterator();
        while (iter.hasNext()) {
            double value = iter.next().getValue();
            int comp = MathUtils.compareTo(value, 0, eps);
            if (comp != 0) {
                return comp;
            }
        }
        return 0;
    }

    public static int compareVectors(RealVector x, RealVector y, double eps)
            throws IllegalArgumentException {
        VectorUtils.checkCompareCompatible(x, y);
        return compareWithZero(x.subtract(y), eps);
    }

    public int compareWithZero(FieldVector<ValueField> v, double eps) {
        ValueField[] data = v.getData();
        for (int i = 0; i < data.length; i++) {
            ValueField value = data[i];
            int comp = fieldHelper.compareTo(value, 0.0, eps);
            if (comp != 0) {
                return comp;
            }
        }
        return 0;
    }
    
    public int compareVectors(FieldVector<ValueField> x, FieldVector<ValueField> y, double eps)
            throws IllegalArgumentException {
        VectorUtils.checkCompareCompatible(x, y);
        return compareWithZero(x.subtract(y), eps);
    }

    public FieldHelper<ValueField> getFieldHelper() {
        return fieldHelper;
    }
    public void setFieldHelper(FieldHelper<ValueField> fieldHelper) {
        this.fieldHelper = fieldHelper;
    }
}
