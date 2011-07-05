package com.dborisenko.math.optimization.problems.generators.params;

import com.dborisenko.math.vo.RangeVO;

/**
 *
 * @author Denis
 */
public class RangeEvalGeneratorParam 
        extends RangeGeneratorParam<String> implements EvalGeneratorParam {

    public RangeEvalGeneratorParam(RangeVO<String> range) {
        super(range);
    }

    public RangeEvalGeneratorParam(String startValue,
            String step, String stopValue) {
        super(startValue, step, stopValue);
    }
}
