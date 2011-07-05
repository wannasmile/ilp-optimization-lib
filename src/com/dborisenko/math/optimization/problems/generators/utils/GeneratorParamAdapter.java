package com.dborisenko.math.optimization.problems.generators.utils;

import com.dborisenko.math.optimization.problems.generators.params.GeneratorParamType;
import com.dborisenko.math.optimization.problems.generators.params.GeneratorParameter;
import com.dborisenko.math.vo.RangeVO;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import net.sourceforge.jeval.EvaluationException;
import net.sourceforge.jeval.Evaluator;

/**
 *
 * @author Denis
 */
public abstract class GeneratorParamAdapter<ValueType extends Number> {

    protected final GeneratorParameter<ValueType> param;

    public GeneratorParamAdapter(GeneratorParameter<ValueType> param) {
        this.param = param;
    }

    public Collection<ValueType> createValuesCollection(Collection<EvalVariableVO> variables)
            throws EvaluationException, IllegalArgumentException {
        GeneratorParamType type = param.getType();
        List<ValueType> result = new ArrayList<ValueType>();

        switch (type) {
            case VALUE:
                result.add(param.getValue());
                break;
            case RANGE:
                result.addAll(createValuesFromRange(param.getRange()));
                break;
            case VALUE_EVAL:
                result.add(evaluateValue(param.getValueEval(), variables));
                break;
            case RANGE_EVAL:
                result.addAll(evaluateRange(param.getRangeEval(), variables));
                break;
        }
        return result;
    }

    protected String evaluate(String eval, Collection<EvalVariableVO> variables)
            throws EvaluationException {
        Evaluator evaluator = new Evaluator();

        if (variables != null) {
            Iterator<EvalVariableVO> iterator = variables.iterator();
            while (iterator.hasNext()) {
                EvalVariableVO var = iterator.next();
                evaluator.putVariable(var.name, var.value);
            }
        }
        return evaluator.evaluate(eval);
    }

    protected Collection<ValueType> evaluateRange(RangeVO<String> evalRange,
            Collection<EvalVariableVO> variables)
            throws EvaluationException, IllegalArgumentException
    {
        RangeVO<ValueType> valuesRange = new RangeVO<ValueType>(
                    evaluateValue(evalRange.getStartValue(), variables),
                    evaluateValue(evalRange.getStep(), variables),
                    evaluateValue(evalRange.getStopValue(), variables)
                );
        return createValuesFromRange(valuesRange);
    }

    public Collection<String> createEvalsCollection() {
        List<String> evals = new ArrayList<String>();
        if (param.getType() == GeneratorParamType.VALUE_EVAL) {
            evals.add(param.getValueEval());
        } else if (param.getType() == GeneratorParamType.RANGE_EVAL) {
            evals.add(param.getRangeEval().getStartValue());
            evals.add(param.getRangeEval().getStep());
            evals.add(param.getRangeEval().getStopValue());
        }
        return evals;
    }

    protected abstract Collection<ValueType> createValuesFromRange(RangeVO<ValueType> range);
    protected abstract ValueType evaluateValue(String evalValue,
            Collection<EvalVariableVO> variables)
            throws EvaluationException, IllegalArgumentException;
}
