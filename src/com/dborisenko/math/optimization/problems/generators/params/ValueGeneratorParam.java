package com.dborisenko.math.optimization.problems.generators.params;

/**
 *
 * @author Denis
 */
public class ValueGeneratorParam<ValueType> implements GeneratorParam {

    private final ValueType value;

    public ValueGeneratorParam(ValueType value) {
        this.value = value;
    }

    public ValueType getValue() {
        return value;
    }
}
