package com.dborisenko.math.utils;

import org.apache.commons.math.util.BigReal;
import org.apache.commons.math.util.BigRealField;

/**
 *
 * @author Denis
 */
public class BigRealHelper extends FieldHelper<BigReal> {

    public BigRealHelper() {
        super(BigRealField.getInstance());
    }

    @Override
    public BigReal fromDouble(double value) {
        return new BigReal(value);
    }

    @Override
    public double toDouble(BigReal value) {
        return value.doubleValue();
    }

}
