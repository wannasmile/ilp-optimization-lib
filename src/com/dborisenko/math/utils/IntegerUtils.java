package com.dborisenko.math.utils;

import org.apache.commons.math.util.MathUtils;

/**
 *
 * @author Denis Borisenko
 */
public class IntegerUtils {

    public static boolean isInteger(final double[] point, final double epsilon) {
        for (int i = 0; i < point.length; i++) {
            if (!isInteger(point[i], epsilon))
                return false;
        }
        return true;
    }

    public static boolean isInteger(final double value, final double epsilon) {
        double intValue = getIntegerPart(value);
        return MathUtils.equals(value, intValue, epsilon);
    }

    public static double getFractionalPart(final double value) {
        return (value - getIntegerPart(value));
    }

    public static double getIntegerPart(final double value) {
        return Math.floor(value);//(value >= 0 ? Math.floor(value) : Math.ceil(value));
    }

    public static double getCeilIntegerPart(final double value) {
        return Math.ceil(value);//(value >= 0 ? Math.floor(value) : Math.ceil(value));
    }
}
