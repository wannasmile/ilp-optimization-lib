package com.dborisenko.math.optimization.problems.generators.params;

import com.dborisenko.math.vo.RangeVO;

/**
 *
 * @author Denis
 */
public class RangeGeneratorParam<ValueType> implements GeneratorParam {

    private final RangeVO<ValueType> range;

    public RangeGeneratorParam(RangeVO<ValueType> range) {
        this.range = range;
    }

    public RangeGeneratorParam(ValueType startValue,
            ValueType step, ValueType stopValue) {
        this(new RangeVO<ValueType>(startValue, step, stopValue));
    }

    public RangeVO<ValueType> getRange() {
        return range;
    }
}
