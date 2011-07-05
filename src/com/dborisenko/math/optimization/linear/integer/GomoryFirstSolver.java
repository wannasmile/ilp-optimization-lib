package com.dborisenko.math.optimization.linear.integer;

import com.dborisenko.math.optimization.linear.LexDualSimplexSolver;
import com.dborisenko.math.optimization.OptimizerPrecision;
import com.dborisenko.math.optimization.linear.integer.gomoryFirstClasses.GomoryFirstLexDualSimplexTableauFactory;
import com.dborisenko.math.optimization.linear.integer.gomoryFirstClasses.GomoryFirstSimplexTableau;
import com.dborisenko.math.optimization.linear.lexDualSimplexClasses.tableaus.SimplexTableau;
import com.dborisenko.math.utils.IntegerUtils;
import javax.persistence.Entity;
import org.apache.commons.math.optimization.OptimizationException;
import org.apache.commons.math.optimization.RealPointValuePair;
import org.apache.commons.math.optimization.linear.UnboundedSolutionException;

/**
 *
 * @author Denis Borisenko
 *
 */
@Entity
public class GomoryFirstSolver extends IntegerLinearSolver {

    private static final long serialVersionUID = 1L;

    private Double simplexValue;
    private Integer simplexIterations;
    private Integer gomoryCuttingCount;

    protected transient LexDualSimplexSolver simplexSolver;
    protected transient GomoryFirstSimplexTableau tableau;

    protected transient Double maxArtificialValue = null;
    protected transient int maxSimplexSolverIterations = Integer.MAX_VALUE;

    private transient RealPointValuePair simplexPoint = null;

    public GomoryFirstSolver() {
    }

    public Integer getGomoryCuttingCount() {
        return gomoryCuttingCount;
    }

    public Integer getSimplexIterations() {
        return simplexIterations;
    }

    public void setMaxArtificialValue(Double value) {
        maxArtificialValue = value;
    }

    public Double getMaxArtificialValue() {
        return maxArtificialValue;
    }

    public void setMaxSimplexSolverIterations(int value) {
        maxSimplexSolverIterations = value;
        if (simplexSolver != null) {
            simplexSolver.setMaxIterations(value);
        }
    }

    public int getMaxSimplexSolverIterations() {
        if (simplexSolver != null) {
            return simplexSolver.getMaxIterations();
        }
        return maxSimplexSolverIterations;
    }

    public RealPointValuePair getSimplexPoint() {
        return simplexPoint;
    }

    public Double getSimplexValue() {
        return simplexValue;
    }

    @Override
    protected void doOptimize()
            throws OptimizationException {
        gomoryCuttingCount = 0;

        simplexSolver = new LexDualSimplexSolver();
        simplexSolver.setTableauFactory(new GomoryFirstLexDualSimplexTableauFactory());
        simplexSolver.setPrecision(OptimizerPrecision.BIG);
        simplexSolver.setEpsilon(getEpsilon());
        simplexSolver.setMaxIterations(maxSimplexSolverIterations);
        simplexSolver.setObjectiveFunction(getObjectiveFunction());
        simplexSolver.setLinearConstraints(getLinearConstraints());
        simplexSolver.setGoalType(getGoalType());


        if (maxArtificialValue != null) {
            simplexSolver.setMaxArtificialValue(maxArtificialValue);
        }

        simplexSolver.optimize();

        simplexPoint = simplexSolver.getOptimalPoint();
        simplexValue = simplexPoint.getValue();

        RealPointValuePair solution = simplexPoint;

        tableau = (GomoryFirstSimplexTableau)simplexSolver.getTableau();

        while (!isOptimal(solution)) {
            solution = doIteration(solution);
        }

        setOptimalPoint(solution);
        simplexIterations = simplexSolver.getIterations();
    }

    protected boolean isOptimal(RealPointValuePair solution) {
        return IntegerUtils.isInteger(solution.getPointRef(), epsilon);
    }

    protected RealPointValuePair doIteration(final RealPointValuePair solution)
            throws OptimizationException {

        incrementIterationsCounter();

        Integer productiveRowIndex = getProductiveRowIndex(tableau);

        if (productiveRowIndex == null) {
            throw new UnboundedSolutionException();
        }

        int artificialIndex = tableau.appendNegativeFractionPartOfRow(productiveRowIndex);
        gomoryCuttingCount++;

//        simplexSolver.doIteration(tableau);
//
//        tableau.removeArtificialRow(artificialIndex);
        
        return simplexSolver.doMajorCycle(tableau);
    }

    protected Integer getProductiveRowIndex(final SimplexTableau tableau) {
        for (int i = 0; i < tableau.getHeight(); i++) {
            if (!IntegerUtils.isInteger(tableau.getRealEntry(i, 0), epsilon)) {
                return i;
            }
        }
        return null;
    }

//    protected double[] createGomoryCuttingRow(final SimplexTableau tableau,
//            final int productiveRowIndex) {
//        double[] result = new double[tableau.getWidth()];
//        for (int j = 0; j < result.length; j++) {
//            result[j] = -IntegerUtils.getFractionalPart(tableau.getRealEntry(productiveRowIndex, j));
//        }
//        gomoryCuttingCount++;
//        return result;
//    }
}
