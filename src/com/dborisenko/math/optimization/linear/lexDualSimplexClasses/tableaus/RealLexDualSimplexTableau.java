package com.dborisenko.math.optimization.linear.lexDualSimplexClasses.tableaus;

import com.dborisenko.math.linear.relations.LexLinearOrder;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math.linear.Array2DRowRealMatrix;
import org.apache.commons.math.linear.ArrayRealVector;
import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.math.linear.RealVector;
import org.apache.commons.math.optimization.GoalType;
import org.apache.commons.math.optimization.linear.LinearConstraint;
import org.apache.commons.math.optimization.linear.LinearObjectiveFunction;
import org.apache.commons.math.optimization.linear.Relationship;
import org.apache.commons.math.util.MathUtils;

/**
 * A tableau for use in the Lexicographical Dual Simplex Method.
 *
 * @author Denis Borisenko
 */
public class RealLexDualSimplexTableau
        extends BaseLexDualSimplexTableau<Double> {

    protected static final double NON_BASE_DECISION_VARIABLES_VALUE = -1;

    /** Serializable version identifier. */
    private static final long serialVersionUID = 1L;

    /** Simple tableau. */
    protected transient Array2DRowRealMatrix tableau;

    protected double zeroColumnsObjectiveSum = 0.0;

    /**
     * Build a tableau for a linear problem.
     */
    public RealLexDualSimplexTableau() {
    }

    @Override
    public void createTableau() {
        super.createTableau();
        this.tableau = createTableau(getGoalType() == GoalType.MAXIMIZE);
    }

    /**
     * Create the tableau by itself.
     * @param maximize if true, goal is to maximize the objective function
     * @return created tableau
     */
    protected Array2DRowRealMatrix createTableau(final boolean maximize) {
        // create a matrix of the correct size
        int width = numDecisionVariables + getNumRightHandSide();
        int height = numDecisionVariables + numSlackVariables +
                numArtificialVariables + getNumObjectiveFunctions();

        Array2DRowRealMatrix matrix = new Array2DRowRealMatrix(height, width);

        initializeObjectiveFunctionEntries(matrix, maximize, getObjectiveFunction());
        initializeNonBaseDecisionVariables(matrix);

        // initialize the coeff rows
        int slackVarIndex = 0;
        int artificialVarIndex = 0;

        for (int i = 0; i < getConstraints().size(); i++) {
            if (initializeLinearConstraintEntries(matrix, getConstraints().get(i), i, slackVarIndex)) {
                ++slackVarIndex;
            }
        }

        // artificial variables
        if (initializeArtificialVariable(matrix, getObjectiveFunction(), artificialVarIndex)) {
            ++artificialVarIndex;
        }

        matrix = initializeZeroColumns(matrix);

//        printTableau(matrix);
        return matrix;
    }

    protected void initializeObjectiveFunctionEntries(final Array2DRowRealMatrix matrix,
            final boolean maximize,
            final LinearObjectiveFunction objectiveFunction) {

        if (getObjectiveFunction() != null) {
            RealVector coefficients =
                maximize ? objectiveFunction.getCoefficients().mapMultiply(-1) : objectiveFunction.getCoefficients();
            matrix.setEntry(getObjectiveFunctionsOffset(),
                    getRightHandSideOffset(),
                    objectiveFunction.getConstantTerm());
            copyArrayRow(coefficients.getData(),
                    matrix.getDataRef()[getObjectiveFunctionsOffset()],
                    getNumRightHandSide());
        }
    }

    protected void initializeNonBaseDecisionVariables(final RealMatrix matrix) {
        for (int i = 0; i < numDecisionVariables; i++) {
            matrix.setEntry(getNumObjectiveFunctions() + i,
                    getNumRightHandSide() + i,
                    NON_BASE_DECISION_VARIABLES_VALUE);
        }
    }

    protected Array2DRowRealMatrix initializeZeroColumns(final Array2DRowRealMatrix matrix) {
        zeroColumnsObjectiveSum = 0.0;
        zeroIndexes = new ArrayList<Integer>();
        zeroColumnsLabels = new ArrayList<String>();

        for (int j = getNumRightHandSide(); j < matrix.getColumnDimension(); j++) {
            boolean isZero = true;
            for (int i = getNumObjectiveFunctions() + getNumDecisionVariables(); i < matrix.getRowDimension() - numArtificialVariables; i++) {
                if (!MathUtils.equals(matrix.getEntry(i, j), 0.0, getEpsilon())) {
                    isZero = false;
                    break;
                }
            }
            if (isZero) {
                zeroColumnsObjectiveSum += ((getGoalType() == GoalType.MAXIMIZE ? 1 : -1 ) *
                    getObjectiveFunction().getCoefficients().getEntry(j - getNumRightHandSide()));
                zeroIndexes.add(j);
                zeroColumnsLabels.add(columnLabels.get(j));
            }
        }
        return getMatrixWithProcessedZeroColumns(zeroIndexes, matrix, zeroColumnsObjectiveSum);
    }

    protected Array2DRowRealMatrix getMatrixWithProcessedZeroColumns(final List<Integer> zeroIndexes,
            final Array2DRowRealMatrix matrix,
            final double zeroColumnsObjectiveSum) {

        if (zeroIndexes.isEmpty()) {
            return matrix;
        }

        updateZeroObjectiveValue(matrix, zeroColumnsObjectiveSum);
        return removeZeroColumns(zeroIndexes, matrix);
    }

    protected void updateZeroObjectiveValue(final RealMatrix matrix,
            final double zeroColumnsObjectiveSum) {
        matrix.setEntry(getObjectiveFunctionsOffset(),
                getRightHandSideOffset(),
                matrix.getEntry(getObjectiveFunctionsOffset(),
                getRightHandSideOffset()) + zeroColumnsObjectiveSum);
    }

    protected Array2DRowRealMatrix removeZeroColumns(final List<Integer> zeroIndexes,
            final Array2DRowRealMatrix matrix) {
        numDecisionVariables -= zeroIndexes.size();

        for (int i = zeroIndexes.size() - 1; i >= 0; i--) {
            int index = (int)zeroIndexes.get(i);
            columnLabels.remove(index);
        }

        Array2DRowRealMatrix resultMatrix = new Array2DRowRealMatrix(matrix.getRowDimension(),
                matrix.getColumnDimension() - zeroIndexes.size());
        int resultJ = 0;
        for (int j = 0; j < matrix.getColumnDimension(); j++) {
            if (!zeroIndexes.contains(j)) {
                resultMatrix.setColumn(resultJ++, matrix.getColumn(j));
            }
        }
        return resultMatrix;
    }

    /**
     *
     * @return true, if slack variable was added.
     *         true, если была добавлена нестрогая переменная
     */
    protected boolean initializeLinearConstraintEntries(final Array2DRowRealMatrix matrix,
            final LinearConstraint constraint,
            final int constraintIndex,
            final int slackIndex) {

        // slack variables
        int slackRowNum = getSlackVariableOffset() + slackIndex;

        if (constraint.getRelationship() == Relationship.LEQ) {
            matrix.setEntry(slackRowNum,
                    getRightHandSideOffset(),
                    constraint.getValue());
            copyArrayRow(constraint.getCoefficients().getData(),
                    matrix.getDataRef()[slackRowNum],
                    getNumRightHandSide());
        } else if (constraint.getRelationship() == Relationship.GEQ) {
            matrix.setEntry(slackRowNum,
                    getRightHandSideOffset(),
                    -1 * constraint.getValue());
            copyArrayRow(constraint.getCoefficients().mapMultiply(-1).getData(),
                    matrix.getDataRef()[slackRowNum],
                    getNumRightHandSide());
        } else {
            return false;
        }
        return true;
    }

    /**
     *
     * @return true, if artificial variable was added.
     *         true, если была добавлена искусственная переменная
     */
    protected boolean initializeArtificialVariable(final Array2DRowRealMatrix matrix,
            final LinearObjectiveFunction objectiveFunction,
            final int artificialIndex) {

        if (!isProblemDualAdmissible()) {
            double constM = calcMaxArtificialValue(objectiveFunction);
            matrix.setEntry(getArtificialVariableOffset() + artificialIndex,
                    getRightHandSideOffset(),
                    constM);
            fillArray(matrix.getDataRef()[getArtificialVariableOffset() + artificialIndex],
                    getNumRightHandSide(),
                    1);
            return true;
        }
        return false;
    }

    protected double calcMaxArtificialValue(final LinearObjectiveFunction objectiveFunction) {
        if (getMaxArtificialValue()== null) {
            return objectiveFunction.getCoefficients().getDimension();
        } else {
            return getMaxArtificialValue();
        }
    }

    private void copyArrayRow(double[] src, double[] dest, int destPos) {
        System.arraycopy(src, 0, dest, destPos, src.length);
    }

    protected void fillArray(double[] array, int arrayPos, double value) {
        for (int i = arrayPos; i < array.length; i++) {
            array[i] = value;
        }
    }

    protected double[] getColumn(int columnIndex) {
        final int nRows = getHeight();
        final double[] out = new double[nRows];
        for (int i = 0; i < nRows; ++i) {
            out[i] = getEntry(i, columnIndex);
        }
        return out;
    }

    protected RealVector getColumnVector(int columnIndex) {
        return new ArrayRealVector(getColumn(columnIndex));
    }

    protected double[] getRow(int rowIndex) {
        final int nCols = getWidth();
        final double[] out = new double[nCols];
        for (int i = 0; i < nCols; ++i) {
            out[i] = getEntry(rowIndex, i);
        }
        return out;
    }

    /**
     * Get the width of the tableau.
     * @return width of the tableau
     */
    public int getWidth() {
        return tableau.getColumnDimension();
    }

    /**
     * Get the height of the tableau.
     * @return height of the tableau
     */
    public int getHeight() {
        return tableau.getRowDimension() + artificialRows.size();
    }

    protected void divideColumn(final int dividendColumn, final double divisor) {
        for (int i = 0; i < getHeight(); i++) {
            setEntry(i, dividendColumn, getEntry(i, dividendColumn) / divisor);
        }
    }

    protected void subtractColumn(final int minuendCol, final int subtrahendCol,
            final double multiple) {
        for (int i = 0; i < getHeight(); i++) {
            setEntry(i,
                    minuendCol,
                    getEntry(i, minuendCol) -
                        multiple * getEntry(i, subtrahendCol));
        }
    }

    public RealVector getDividedColumn(final int col, final double value) {
        return getColumnVector(col).mapDivide(value);
    }

    public int lexCompareVectors(final RealVector x, final RealVector y, final double epsilon) {
        return LexLinearOrder.compareVectors(x, y, epsilon);
    }

    /** Get an entry of the tableau.
     * @param row row index
     * @param column column index
     * @return entry at (row, column)
     */
    public Double getEntry(final int row, final int column) {
        if (row >= tableau.getRowDimension() && row < tableau.getRowDimension() + artificialRows.size()) {
            return artificialRows.get(row - tableau.getRowDimension())[column];
        }
        return tableau.getEntry(row, column);
    }

    /** Set an entry of the tableau.
     * @param row row index
     * @param column column index
     * @param value for the entry
     */
    public void setEntry(final int row, final int column,
            final Double value) {
        if (row >= tableau.getRowDimension() && row < tableau.getRowDimension() + artificialRows.size()) {
            artificialRows.get(row - tableau.getRowDimension())[column] = value;
            return;
        }
        tableau.setEntry(row, column, value);
    }

    public void doSimplexIteration(int p, int q) {

        double entryPQ = getEntry(p, q);
        double[] rowP = getRow(p);
        double[] columnQ = getColumn(q);

        for (int j = 0; j < getWidth(); j++) {
            for (int i = 0; i < getHeight(); i++) {
                if (i == p && j == q) {
                    setEntry(p, q, 1 / entryPQ);
                } else if (i == p) {
                    setEntry(p, j, rowP[j] / entryPQ);
                } else if (j == q) {
                    setEntry(i, q, - columnQ[i] / entryPQ);
                } else {
                    setEntry(i, j, getEntry(i, j) - ((columnQ[i] * rowP[j] / entryPQ)));
                }
            }
        }
    }

    public double getRealEntry(int row, int column) {
        return getEntry(row, column);
    }

    public Integer getPivotColumn(Integer row, boolean ignoreNegotiveCondition) {
        if (row == null) {
            return null;
        }

        Integer p = row;
        Integer q = null;
        RealVector minVector = null;

        for (int j = getNumRightHandSide(); j < getWidth(); j++) {
            if (ignoreNegotiveCondition || MathUtils.compareTo(getEntry(p, j), 0, getEpsilon()) < 0) {

                RealVector column = getDividedColumn(j, Math.abs(getEntry(p, j)));
                if (minVector == null || lexCompareVectors(column, minVector, getEpsilon()) < 0) {
                    minVector = column;
                    q = j;
                }
            }
        }

        return q;
    }
}