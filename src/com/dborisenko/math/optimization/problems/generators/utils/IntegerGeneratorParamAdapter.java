/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

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
public class IntegerGeneratorParamAdapter extends GeneratorParamAdapter<Integer> {

    public IntegerGeneratorParamAdapter(GeneratorParameter<Integer> param) {
        super(param);
    }
    
    @Override
    protected Collection<Integer> createValuesFromRange(RangeVO<Integer> range) {
        List<Integer> result = new ArrayList<Integer>();
        int coeff = (range.getStopValue() >= range.getStartValue() ? 1 : -1);
        
        for (Integer i = range.getStartValue(); coeff * i <= coeff * range.getStopValue(); i += range.getStep()) {
            result.add(i);
        }
        return result;
    }

    @Override
    protected Integer evaluateValue(String evalValue, 
            Collection<EvalVariableVO> variables)
            throws EvaluationException, IllegalArgumentException {
        String result = evaluate(evalValue, variables);
        return Math.round(Float.parseFloat(result));
    }


}
