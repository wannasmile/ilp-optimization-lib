/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dborisenko.math.optimization.problems.generators;

import com.dborisenko.math.optimization.problems.LinearOptimizationProblem;
import com.dborisenko.math.optimization.problems.SetPackingProblem;
import com.dborisenko.math.optimization.problems.generators.params.GeneratorParameter;
import com.dborisenko.math.optimization.problems.generators.utils.CrossReferencesUtils;
import com.dborisenko.math.optimization.problems.generators.utils.DoubleGeneratorParamAdapter;
import com.dborisenko.math.optimization.problems.generators.utils.EvalVariableVO;
import com.dborisenko.math.optimization.problems.generators.utils.IntegerGeneratorParamAdapter;
import com.dborisenko.math.optimization.problems.generators.utils.NamedCollectionVO;
import com.dborisenko.math.optimization.problems.metadata.SetPackingProblemMetadata;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import net.sourceforge.jeval.EvaluationException;
import org.apache.commons.math.linear.Array2DRowRealMatrix;
import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.math.random.JDKRandomGenerator;
import org.apache.commons.math.random.RandomGenerator;

/**
 *
 * @author Denis
 */
public class UnweightedSetPackingProblemGenerator implements ProblemGenerator {
    public static final String VARIABLE_NAME_M = "#{m}";
    public static final String VARIABLE_NAME_N = "#{n}";
    public static final String VARIABLE_NAME_P = "#{p}";

    protected RandomGenerator randomGenerator = new JDKRandomGenerator();
    protected GeneratorParameter<Integer> variablesCountN = new GeneratorParameter<Integer>();
    protected GeneratorParameter<Integer> constraintsCountM = new GeneratorParameter<Integer>();
    protected GeneratorParameter<Double> probabilityOfOne = new GeneratorParameter<Double>();

    protected int problemsCount = 1;

    private transient IntegerGeneratorParamAdapter nAdapter;
    private transient IntegerGeneratorParamAdapter mAdapter;
    private transient DoubleGeneratorParamAdapter pAdapter;

    private transient NamedCollectionVO<String> nEvals;
    private transient NamedCollectionVO<String> mEvals;
    private transient NamedCollectionVO<String> pEvals;

    public UnweightedSetPackingProblemGenerator() {
        randomGenerator.setSeed((new Date().getTime()));
    }

    public GeneratorParameter<Integer> getVariablesCountN() {
        return variablesCountN;
    }

    public GeneratorParameter<Integer> getConstraintsCountM() {
        return constraintsCountM;
    }

    public GeneratorParameter<Double> getProbabilityOfOne() {
        return probabilityOfOne;
    }

    public void setProblemsCount(int value) {
        problemsCount = value;
    }

    public int getProblemsCount() {
        return problemsCount;
    }

    public Collection<EvalVariableVO> createVariables(Integer n, Integer m, Double p) {
        List<EvalVariableVO> result = new ArrayList<EvalVariableVO>();
        if (n != null) {
            result.add(new EvalVariableVO("n", String.valueOf(n)));
        }
        if (m != null) {
            result.add(new EvalVariableVO("m", String.valueOf(m)));
        }
        if (p != null) {
            result.add(new EvalVariableVO("p", String.valueOf(p)));
        }
        return result;
    }

    public LinearOptimizationProblem generateProblem(int variablesCountN,
            int constraintsCountM,
            double probabilityOfOne) {
        RealMatrix constraintsMatrixA = new Array2DRowRealMatrix(constraintsCountM, variablesCountN);

        for (int i = 0; i < constraintsMatrixA.getRowDimension(); i++)
        {
            for (int j = 0; j < constraintsMatrixA.getColumnDimension(); j++)
            {
                constraintsMatrixA.setEntry(i, j,
                        generateOneOrZeroWithProbability(probabilityOfOne));
            }
        }

        return new SetPackingProblem(constraintsMatrixA);
    }

    private void addProblemToResult(Integer n, Integer m, Double p, 
            int nIndex, int mIndex, int pIndex,
            List<ProblemAndMetadataPair> result) {
//        System.out.println("val; n=" + n + "; m=" + m + "; p=" + p + "; count=" + problemsCount);

        for (int i = 0; i < problemsCount; i++) {
            try {
                LinearOptimizationProblem problem = generateProblem(n, m, p);
                SetPackingProblemMetadata metadata = new SetPackingProblemMetadata();
                metadata.setEvalVariablesCountN(readEvalValue(variablesCountN, nIndex));
                metadata.setEvalConstraintsCountM(readEvalValue(constraintsCountM, mIndex));
                metadata.setEvalProbabilityOfOne(readEvalValue(probabilityOfOne, pIndex));
                metadata.setProbabilityOfOne(p);
                result.add(new ProblemAndMetadataPair(problem, metadata));
            } catch (Exception ex) {
            }
        }
    }

    private double generateOneOrZeroWithProbability(double probabilityOfOne)
    {
        if (randomGenerator.nextDouble() < probabilityOfOne)
            return 1.0;
        return 0.0;
    }


    protected void generateProblemsRec(List<NamedCollectionVO<String>> list,
            int index,
            List<ProblemAndMetadataPair> result,
            Integer n, Integer m, Double p,
            int nIndex, int mIndex, int pIndex)
            throws IllegalArgumentException, EvaluationException {

        if (index < list.size()) {
            NamedCollectionVO<String> item = list.get(index);
            index++;
            Collection<EvalVariableVO> variables = createVariables(n, m, p);
//            System.out.println("rec; n=" + n + "; m=" + m + "; p=" + p + "; curr:" + item.toString());
            if (item == nEvals) {
                Collection<Integer> nValues = nAdapter.createValuesCollection(variables);
                Iterator<Integer> valuesIterator = nValues.iterator();
                nIndex = 0;
                while (valuesIterator.hasNext()) {
                    generateProblemsRec(list, index, result, valuesIterator.next(), m, p,
                            nIndex++, mIndex, pIndex);
                }
            } else if (item == mEvals) {
                Collection<Integer> mValues = mAdapter.createValuesCollection(variables);
                Iterator<Integer> valuesIterator = mValues.iterator();
                mIndex = 0;
                while (valuesIterator.hasNext()) {
                    generateProblemsRec(list, index, result, n, valuesIterator.next(), p,
                            nIndex, mIndex++, pIndex);
                }
            } else if (item == pEvals) {
                Collection<Double> pValues = pAdapter.createValuesCollection(variables);
                Iterator<Double> valuesIterator = pValues.iterator();
                pIndex = 0;
                while (valuesIterator.hasNext()) {
                    generateProblemsRec(list, index, result, n, m, valuesIterator.next(),
                            nIndex, mIndex, pIndex++);
                }
            }
        } else if (n != null && m != null && p != null) {
            addProblemToResult(n, m, p, nIndex, mIndex, pIndex, result);
        }
    }

    private String readEvalValue(GeneratorParameter param, int index) {
        String result = null;
        switch (param.getType()) {
            case VALUE_EVAL:
                result = param.getValueEval();
                break;
            case RANGE_EVAL:
                result = "(" + param.getRangeEval().getStartValue() + ")" +
                        "+" + index + "*" +
                        "(" + param.getRangeEval().getStep() + ")";
                break;
        }
        return result;
    }

    @Override
    public List<ProblemAndMetadataPair> generateProblems()
            throws Exception {

         List<ProblemAndMetadataPair> result =
                new ArrayList<ProblemAndMetadataPair>();
         
        nAdapter = new IntegerGeneratorParamAdapter(variablesCountN);
        mAdapter = new IntegerGeneratorParamAdapter(constraintsCountM);
        pAdapter = new DoubleGeneratorParamAdapter(probabilityOfOne);

        nEvals = new NamedCollectionVO<String>(VARIABLE_NAME_N, nAdapter.createEvalsCollection());
        mEvals = new NamedCollectionVO<String>(VARIABLE_NAME_M, mAdapter.createEvalsCollection());
        pEvals = new NamedCollectionVO<String>(VARIABLE_NAME_P, pAdapter.createEvalsCollection());

        List<NamedCollectionVO<String>> evals = new ArrayList<NamedCollectionVO<String>>();
        evals.add(nEvals);
        evals.add(mEvals);
        evals.add(pEvals);

        List<NamedCollectionVO<String>> orderedEvals =
                CrossReferencesUtils.orderByIndependence(evals);

        generateProblemsRec(orderedEvals, 0, result, null, null, null, 0, 0, 0);

        return result;
    }
}
