package com.dborisenko.math.optimization.problems.generators.params;

import com.dborisenko.math.vo.RangeVO;

/**
 *
 * @author Denis
 */
public class GeneratorParameter<ValueType extends Number> {

    private GeneratorParam impl;

    protected GeneratorParam getImpl() {
        return impl;
    }
    protected void setImpl(GeneratorParam impl) {
        this.impl = impl;
    }

    public GeneratorParamType getType() {
        if (impl instanceof RangeGeneratorParam) {
            return (impl instanceof EvalGeneratorParam) ? 
                GeneratorParamType.RANGE_EVAL :
                GeneratorParamType.RANGE;
        } else if (impl instanceof ValueGeneratorParam) {
            return (impl instanceof EvalGeneratorParam) ?
                GeneratorParamType.VALUE_EVAL :
                GeneratorParamType.VALUE;
        }
        return null;
    }

    public boolean isEval() {
        return (impl instanceof EvalGeneratorParam);
    }

    public void setValue(ValueType value) {
        setImpl(new ValueGeneratorParam<ValueType>(value));
    }

    public void setValueEval(String value) {
        setImpl(new ValueEvalGeneratorParam(value));
    }

    public void setRange(RangeVO<ValueType> range) {
        setImpl(new RangeGeneratorParam<ValueType>(range));
    }

    public void setRange(ValueType startValue,
            ValueType step, ValueType stopValue) {
        setImpl(new RangeGeneratorParam<ValueType>(startValue, step, stopValue));
    }

    public void setRangeEval(RangeVO<String> range) {
        setImpl(new RangeEvalGeneratorParam(range));
    }

    public void setRangeEval(String startValue,
            String step, String stopValue) {
        setImpl(new RangeEvalGeneratorParam(startValue, step, stopValue));
    }

    public ValueType getValue() {
        if (impl instanceof ValueGeneratorParam) {
            return ((ValueGeneratorParam<ValueType>)impl).getValue();
        }
        return null;
    }

    public String getValueEval() {
        if (impl instanceof ValueEvalGeneratorParam) {
            return ((ValueEvalGeneratorParam)impl).getValue();
        }
        return null;
    }

    public RangeVO<ValueType> getRange() {
        if (impl instanceof RangeGeneratorParam) {
            return ((RangeGeneratorParam<ValueType>)impl).getRange();
        }
        return null;
    }

    public RangeVO<String> getRangeEval() {
        if (impl instanceof RangeEvalGeneratorParam) {
            return ((RangeEvalGeneratorParam)impl).getRange();
        }
        return null;
    }
}
