package com.dborisenko.math.optimization.linear;

import com.dborisenko.math.optimization.OptimizerPrecision;
import com.dborisenko.math.optimization.linear.lexDualSimplexClasses.factories.LexDualSimplexTableauFactory;
import com.dborisenko.math.optimization.linear.lexDualSimplexClasses.tableaus.SimplexTableau;
import com.dborisenko.math.optimization.linear.lexDualSimplexClasses.factories.SimplexTableauFactory;
import com.dborisenko.math.optimization.linear.lexDualSimplexClasses.ZeroColumnsAction;
import javax.persistence.Entity;
import org.apache.commons.math.optimization.OptimizationException;
import org.apache.commons.math.optimization.RealPointValuePair;
import org.apache.commons.math.optimization.linear.UnboundedSolutionException;

/**
 * Solves a linear integer problem using the Lexicographical Dual Simplex Method.
 * @author Denis Borisenko
 */
@Entity
public class LexDualSimplexSolver
        extends LinearSolver {

    protected transient Double maxArtificialValue = null;

    protected transient SimplexTableau tableau;

    private transient ZeroColumnsAction zeroColumnsAction = ZeroColumnsAction.ADD_CONSTRAINTS;

    private transient OptimizerPrecision precision = OptimizerPrecision.DOUBLE;

    private transient SimplexTableauFactory tableauFactory = new LexDualSimplexTableauFactory();

    /**
     * Build a simplex solver with default settings.
     */
    public LexDualSimplexSolver()
    {
    }

    public void setMaxArtificialValue(Double value) {
        maxArtificialValue = value;
    }

    public Double getMaxArtificialValue() {
        return maxArtificialValue;
    }

    public SimplexTableau getTableau() {
        return tableau;
    }

    protected SimplexTableau createTableau() {
        return getTableauFactory().createTableau(getZeroColumnsAction(), getPrecision());
    }

    public void initTableau() {
        if (tableau == null) {
            tableau = createTableau();
            tableau.setObjectiveFunction(function);
            tableau.setConstraints(linearConstraints);
            tableau.setGoalType(goalType);
            tableau.setEpsilon(epsilon);

            if (getMaxArtificialValue() != null) {
                tableau.setMaxArtificialValue(getMaxArtificialValue());
            }

            tableau.createTableau();
        }
    }

    /** {@inheritDoc} */
    @Override
    public void doOptimize()
            throws OptimizationException {
        
        initTableau();
//        System.out.println("Created tableau: \n" + tableau.toString());

        RealPointValuePair solution = doCycle(tableau);
        setOptimalPoint(solution);
    }

    public RealPointValuePair doCycle(SimplexTableau tableau)
            throws OptimizationException {
        doPreCycle(tableau);
        return doMajorCycle(tableau);
    }

    public void doPreCycle(SimplexTableau tableau)
            throws OptimizationException {
        
        if (!tableau.isProblemDualAdmissible()) {
            Integer pivotRow = tableau.getArtificialVariableOffset();
            Integer pivotCol = getPivotColumn(tableau, pivotRow, true);
            doIteration(tableau, pivotRow, pivotCol);
            tableau.setProblemDualAdmissible(false);
        }
    }

    public RealPointValuePair doMajorCycle(SimplexTableau tableau)
            throws OptimizationException {

        while (!tableau.isOptimal()) {
            doIteration(tableau);
        }

        return tableau.getSolution();
    }

    /**
     * Runs one iteration of the Simplex method on the given model.
     * @param tableau simple tableau for the problem
     * @throws OptimizationException if the maximal iteration count has been
     * exceeded or if the model is found not to have a bounded solution
     */
    public void doIteration(final SimplexTableau tableau)
        throws OptimizationException {

        incrementIterationsCounter();

        Integer pivotRow = getPivotRow(tableau);
        Integer pivotCol = getPivotColumn(tableau, pivotRow);
        
        doIteration(tableau, pivotRow, pivotCol);
    }

    protected void doIteration(final SimplexTableau tableau,
            Integer pivotRow, Integer pivotCol) 
        throws OptimizationException {

        if (pivotCol == null) {
            throw new UnboundedSolutionException();
        }

        tableau.replaceLabels(pivotRow, pivotCol);

        tableau.doSimplexIteration(pivotRow, pivotCol);
//        System.out.println("After iteration #" + getIterations() + ": [" +
//                pivotRow + "," + pivotCol + "]\n" + tableau.toString());
    }

    private Integer getPivotRow(SimplexTableau tableau) {
        return tableau.getPivotRow();
    }

    private Integer getPivotColumn(SimplexTableau tableau,
            final Integer row) {
        return getPivotColumn(tableau, row, false);
    }

    private Integer getPivotColumn(SimplexTableau tableau,
            final Integer row, final boolean ignoreNegotiveCondition) {
        return tableau.getPivotColumn(row, ignoreNegotiveCondition);
    }

    public ZeroColumnsAction getZeroColumnsAction() {
        return zeroColumnsAction;
    }
    public void setZeroColumnsAction(ZeroColumnsAction zeroColumnsAction) {
        this.zeroColumnsAction = zeroColumnsAction;
    }

    public OptimizerPrecision getPrecision() {
        return precision;
    }
    public void setPrecision(OptimizerPrecision precision) {
        this.precision = precision;
    }

    /**
     * @return the tableauFactory
     */
    public SimplexTableauFactory getTableauFactory() {
        return tableauFactory;
    }

    /**
     * @param tableauFactory the tableauFactory to set
     */
    public void setTableauFactory(SimplexTableauFactory tableauFactory) {
        this.tableauFactory = tableauFactory;
    }
}
