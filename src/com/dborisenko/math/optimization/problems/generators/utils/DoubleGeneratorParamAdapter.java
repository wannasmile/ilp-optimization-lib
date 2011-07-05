package com.dborisenko.math.optimization.problems.generators.utils;

import com.dborisenko.math.optimization.problems.generators.params.GeneratorParameter;
import com.dborisenko.math.vo.RangeVO;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.sourceforge.jeval.EvaluationException;

/**
 *
 * @author Denis
 */
public class DoubleGeneratorParamAdapter extends GeneratorParamAdapter<Double> {

    public DoubleGeneratorParamAdapter(GeneratorParameter<Double> param) {
        super(param);
    }
    
    @Override
    protected Collection<Double> createValuesFromRange(RangeVO<Double> range) {
        List<Double> result = new ArrayList<Double>();
        double coeff = (range.getStopValue() >= range.getStartValue() ? 1.0 : -1.0);

        for (Double d = range.getStartValue(); coeff * d <= coeff * range.getStopValue(); d += range.getStep()) {
            result.add(d);
        }
        return result;
    }

    @Override
    protected Double evaluateValue(String evalValue, Collection<EvalVariableVO> variables)
            throws EvaluationException, IllegalArgumentException {
        String result = evaluate(evalValue, variables);
        return Double.parseDouble(result);
    }

}
