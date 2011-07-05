package com.dborisenko.math.optimization.linear.lexDualSimplexClasses.tableaus;

import com.dborisenko.math.linear.relations.LexLinearOrder;
import com.dborisenko.math.utils.FieldHelper;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math.FieldElement;
import org.apache.commons.math.linear.Array2DRowFieldMatrix;
import org.apache.commons.math.linear.ArrayFieldVector;

import org.apache.commons.math.linear.FieldMatrix;
import org.apache.commons.math.linear.FieldVector;
import org.apache.commons.math.linear.InvalidMatrixException;
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
public class LexDualSimplexTableau<ValueField extends FieldElement<ValueField>>
        extends BaseLexDualSimplexTableau<ValueField> {

    /** Serializable version identifier. */
    private static final long serialVersionUID = 1L;
    
    private FieldHelper<ValueField> fieldHelper;

    /** Simple tableau. */
    protected transient FieldMatrix<ValueField> tableau;

    protected ValueField zeroColumnsObjectiveSum;
    
    protected LexLinearOrder<ValueField> lexLinearOrder = new LexLinearOrder<ValueField>();

    /**
     * Build a tableau for a linear problem.
     */
    public LexDualSimplexTableau() {
    }

    @Override
    public void createTableau() {
        super.createTableau();
        this.tableau = createTableau(getGoalType() == GoalType.MAXIMIZE);
    }

    protected ValueField getNonBaseDecisionVariablesValue() {
        return fieldHelper.getMinusOne();
    }

    protected FieldMatrix<ValueField> createMatrix(int rowDimension, int columnDimension) {
        return new Array2DRowFieldMatrix<ValueField>(fieldHelper.getField(), rowDimension, columnDimension);
    }

    /**
     * Create the tableau by itself.
     * @param maximize if true, goal is to maximize the objective function
     * @return created tableau
     */
    protected FieldMatrix<ValueField> createTableau(final boolean maximize) {
        // create a matrix of the correct size
        int width = numDecisionVariables + getNumRightHandSide();
        int height = numDecisionVariables + numSlackVariables +
                numArtificialVariables + getNumObjectiveFunctions();

        FieldMatrix<ValueField> matrix = createMatrix(height, width);

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
        
        return matrix;
    }

    protected void initializeObjectiveFunctionEntries(final FieldMatrix<ValueField> matrix,
            final boolean maximize,
            final LinearObjectiveFunction objectiveFunction) {

        if (getObjectiveFunction() != null) {
            RealVector coefficients =
                maximize ? objectiveFunction.getCoefficients().mapMultiply(-1) : objectiveFunction.getCoefficients();
            matrix.setEntry(getObjectiveFunctionsOffset(),
                    getRightHandSideOffset(),
                    fieldHelper.fromDouble(objectiveFunction.getConstantTerm()));
            copyRow(coefficients.getData(), matrix,
                    getObjectiveFunctionsOffset(), getNumRightHandSide());
        }
    }

    protected void initializeNonBaseDecisionVariables(final FieldMatrix<ValueField> matrix) {
        for (int i = 0; i < numDecisionVariables; i++) {
            matrix.setEntry(getNumObjectiveFunctions() + i,
                    getNumRightHandSide() + i,
                    getNonBaseDecisionVariablesValue());
        }
    }

    protected FieldMatrix<ValueField> initializeZeroColumns(final FieldMatrix<ValueField> matrix) {
        zeroColumnsObjectiveSum = fieldHelper.getZero();
        zeroIndexes = new ArrayList<Integer>();
        zeroColumnsLabels = new ArrayList<String>();

        for (int j = getNumRightHandSide(); j < matrix.getColumnDimension(); j++) {
            boolean isZero = true;
            for (int i = getNumObjectiveFunctions() + getNumDecisionVariables(); i < matrix.getRowDimension() - numArtificialVariables; i++) {
                ValueField value = matrix.getEntry(i, j);
                if (!fieldHelper.equals(value, 0.0, getEpsilon())) {
                    isZero = false;
                    break;
                }
            }
            if (isZero) {
                zeroColumnsObjectiveSum = zeroColumnsObjectiveSum.add(
                        fieldHelper.fromDouble(getObjectiveFunction().getCoefficients().getEntry(
                            j - getNumRightHandSide())));
                zeroIndexes.add(j);
                zeroColumnsLabels.add(columnLabels.get(j));
            }
        }
        return getMatrixWithProcessedZeroColumns(zeroIndexes, matrix, zeroColumnsObjectiveSum);
    }

    protected FieldMatrix<ValueField> getMatrixWithProcessedZeroColumns(final List<Integer> zeroIndexes,
            final FieldMatrix<ValueField> matrix,
            final ValueField zeroColumnsObjectiveSum) {

        if (zeroIndexes.size() == 0) {
            return matrix;
        }

        updateZeroObjectiveValue(matrix, zeroColumnsObjectiveSum);
        return removeZeroColumns(zeroIndexes, matrix);
    }

    protected void updateZeroObjectiveValue(final FieldMatrix<ValueField> matrix,
            final ValueField zeroColumnsObjectiveSum) {
        matrix.setEntry(getObjectiveFunctionsOffset(),
                getRightHandSideOffset(),
                matrix.getEntry(getObjectiveFunctionsOffset(),
                getRightHandSideOffset()).add(zeroColumnsObjectiveSum));
    }

    protected FieldMatrix<ValueField> removeZeroColumns(final List<Integer> zeroIndexes,
            final FieldMatrix<ValueField> matrix) {

        for (int i = zeroIndexes.size() - 1; i >= 0; i--) {
            int index = (int)zeroIndexes.get(i);
            columnLabels.remove(index);
        }
        
        FieldMatrix<ValueField> resultMatrix = createMatrix(matrix.getRowDimension(),
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
    protected boolean initializeLinearConstraintEntries(final FieldMatrix<ValueField> matrix,
            final LinearConstraint constraint, 
            final int constraintIndex,
            final int slackIndex) {

        // slack variables
        int slackRowNum = getSlackVariableOffset() + slackIndex;

        if (constraint.getRelationship() == Relationship.LEQ) {
            matrix.setEntry(slackRowNum,
                    getRightHandSideOffset(),
                    fieldHelper.fromDouble(constraint.getValue()));
            copyRow(constraint.getCoefficients().getData(), matrix,
                    slackRowNum, getNumRightHandSide());
        } else if (constraint.getRelationship() == Relationship.GEQ) {
            matrix.setEntry(slackRowNum,
                    getRightHandSideOffset(),
                    fieldHelper.fromDouble(-1 * constraint.getValue()));
            copyRow(constraint.getCoefficients().mapMultiply(-1).getData(), matrix,
                    slackRowNum, getNumRightHandSide());
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
    protected boolean initializeArtificialVariable(final FieldMatrix<ValueField> matrix,
            final LinearObjectiveFunction objectiveFunction,
            final int artificialIndex) {

        if (!isProblemDualAdmissible()) {
            ValueField constM = calcMaxArtificialValue(objectiveFunction);
            matrix.setEntry(getArtificialVariableOffset() + artificialIndex, 
                    getRightHandSideOffset(),
                    constM);
            fillRow(matrix, getArtificialVariableOffset() + artificialIndex, 
                    getNumRightHandSide(), fieldHelper.getOne());
            return true;
        }
        return false;
    }

    private ValueField calcMaxArtificialValue(final LinearObjectiveFunction objectiveFunction) {
        if (getMaxArtificialValue()== null) {
            return fieldHelper.fromDouble(objectiveFunction.getCoefficients().getDimension());
        } else {
            return fieldHelper.fromDouble(getMaxArtificialValue());
        }
    }

    protected void copyRow(double[] src, FieldMatrix<ValueField> matrix, int row, int destStartPos) {
        for (int j = 0; j < src.length; j++) {
            matrix.setEntry(row, j + destStartPos, fieldHelper.fromDouble(src[j]));
        }
    }

    protected void copyRow(ValueField[] src, FieldMatrix<ValueField> matrix, int row, int destStartPos) {
        for (int j = 0; j < src.length; j++) {
            matrix.setEntry(row, j + destStartPos, src[j]);
        }
    }

    protected void copyRow(List<ValueField> src, FieldMatrix<ValueField> matrix, int row, int destStartPos) {
        for (int j = 0; j < src.size(); j++) {
            matrix.setEntry(row, j + destStartPos, src.get(j));
        }
    }

    protected void fillRow(FieldMatrix<ValueField> matrix, int row, int destStartPos, ValueField value) {
        for (int j = destStartPos; j < matrix.getColumnDimension(); j++) {
            matrix.setEntry(row, j, value);
        }
    }

    public FieldVector<ValueField> getColumnVector(int columnIndex) {
        final int nRows = getHeight();
        final FieldVector<ValueField> out = new ArrayFieldVector<ValueField>(nRows, fieldHelper.getZero());
        for (int i = 0; i < nRows; ++i) {
            out.setEntry(i, getEntry(i, columnIndex));
        }
        return out;
    }

    public FieldVector<ValueField> getRowVector(int rowIndex) {
        final int nCols = getWidth();
        final FieldVector<ValueField> out = new ArrayFieldVector<ValueField>(nCols, fieldHelper.getZero());
        for (int i = 0; i < nCols; ++i) {
            out.setEntry(i, getEntry(rowIndex, i));
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

    protected void divideColumn(final int dividendColumn, final ValueField divisor) {
        for (int i = 0; i < getHeight(); i++) {
            setEntry(i, dividendColumn, getEntry(i, dividendColumn).divide(divisor));
        }
    }

    protected void subtractColumn(final int minuendCol, final int subtrahendCol,
            final ValueField multiple) {
        for (int i = 0; i < getHeight(); i++) {
            setEntry(i,
                    minuendCol,
                    getEntry(i, minuendCol).subtract(multiple.multiply(
                        getEntry(i, subtrahendCol))));
        }
    }

    public FieldVector<ValueField> getDividedColumn(final int col, final double value) {
        return getDividedColumn(col, fieldHelper.fromDouble(value));
    }

    public FieldVector<ValueField> getDividedColumn(final int col, final ValueField value) {
        return getColumnVector(col).mapDivide(value);
    }

    public int lexCompareVectors(final FieldVector<ValueField> x,
            FieldVector<ValueField> y) {
        return lexLinearOrder.compareVectors(x, y, getEpsilon());
    }

    public void doSimplexIteration(int p, int q) {
        ValueField entryPQ = getEntry(p, q);
        FieldVector<ValueField> rowP = getRowVector(p);
        FieldVector<ValueField> columnQ = getColumnVector(q);

        for (int j = 0; j < getWidth(); j++) {
            for (int i = 0; i < getHeight(); i++) {
                if (i == p && j == q) {
                    setEntry(p, q, fieldHelper.getOne().divide(entryPQ));
                } else if (i == p) {
                    setEntry(p, j, rowP.getEntry(j).divide(entryPQ));
                } else if (j == q) {
                    setEntry(i, q, fieldHelper.getNegative(columnQ.getEntry(i).divide(entryPQ)));
                } else {
                    setEntry(i, j, getEntry(i, j).subtract(((columnQ.getEntry(i).multiply(rowP.getEntry(j)).divide(entryPQ)))));
                }
            }
        }
    }

    /** Get an entry of the tableau.
     * @param row row index
     * @param column column index
     * @return entry at (row, column)
     */
    public ValueField getEntry(final int row, final int column) {
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
            final ValueField value) {
        if (row >= tableau.getRowDimension() && row < tableau.getRowDimension() + artificialRows.size()) {
            artificialRows.get(row - tableau.getRowDimension())[column] = value;
            return;
        }
        tableau.setEntry(row, column, value);
    }

    public double getRealEntry(final int row, final int column) {
        return getFieldHelper().toDouble(getEntry(row, column));
    }

    public FieldHelper<ValueField> getFieldHelper() {
        return fieldHelper;
    }
    public void setFieldHelper(FieldHelper<ValueField> fieldHelper) {
        this.fieldHelper = fieldHelper;
        lexLinearOrder.setFieldHelper(fieldHelper);
    }

    public Integer getPivotColumn(Integer row, boolean ignoreNegotiveCondition) {
        if (row == null) {
            return null;
        }

        Integer p = row;
        Integer q = null;
        FieldVector<ValueField> minVector = null;

        for (int j = getNumRightHandSide(); j < getWidth(); j++) {
            double val = getRealEntry(p, j);
            if (ignoreNegotiveCondition || MathUtils.compareTo(val, 0, getEpsilon()) < 0) {

                FieldVector<ValueField> column = getDividedColumn(j, Math.abs(val));
                if (minVector == null || lexCompareVectors(column, minVector) < 0) {
                    minVector = column;
                    q = j;
                }
            }
        }

        return q;
    }

    public int appendArtificialRow(Double[] doubleRow) {
        int index = getHeight();
        ValueField[] row = getFieldHelper().buildArray(doubleRow.length);
        for (int i = 0; i < row.length; i++) {
            row[i] = getFieldHelper().fromDouble(doubleRow[i]);
        }
        artificialRows.add(row);
        rowLabels.add(ARTIFICIAL_VARIABLES_LABEL_PREFIX + numArtificialVariables++);
        return index;
    }
}
