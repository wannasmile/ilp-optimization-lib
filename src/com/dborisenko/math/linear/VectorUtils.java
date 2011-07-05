package com.dborisenko.math.linear;

import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.linear.FieldVector;
import org.apache.commons.math.linear.RealVector;

/**
 *
 * @author Denis Borisenko
 */
public class VectorUtils {

    public static void checkCompareCompatible(final RealVector left, final RealVector right)
        throws IllegalArgumentException {
        if (left.getDimension() != right.getDimension()) {
            throw MathRuntimeException.createIllegalArgumentException(
                    "{0}x{1} and {2}x{3} matrices are not compare compatible",
                    left.getDimension(), right.getDimension());
        }
    }

    public static void checkCompareCompatible(final FieldVector left, final FieldVector right)
        throws IllegalArgumentException {
        if (left.getDimension() != right.getDimension()) {
            throw MathRuntimeException.createIllegalArgumentException(
                    "{0}x{1} and {2}x{3} matrices are not compare compatible",
                    left.getDimension(), right.getDimension());
        }
    }

}
