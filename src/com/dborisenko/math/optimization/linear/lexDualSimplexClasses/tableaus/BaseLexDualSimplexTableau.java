/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dborisenko.math.optimization.linear.lexDualSimplexClasses.tableaus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.math.linear.RealVector;
import org.apache.commons.math.optimization.GoalType;
import org.apache.commons.math.optimization.RealPointValuePair;
import org.apache.commons.math.optimization.linear.LinearConstraint;
import org.apache.commons.math.optimization.linear.LinearObjectiveFunction;
import org.apache.commons.math.optimization.linear.Relationship;
import org.apache.commons.math.util.MathUtils;

/**
 *
 * @author Denis
 */
public abstract class BaseLexDualSimplexTableau<ValueField>
        implements Serializable, SimplexTableau<ValueField> {

    protected static final String NON_BASE_DECISION_VARIABLES_LABEL_PREFIX = "nx";
    protected static final String DECISION_VARIABLES_LABEL_PREFIX = "x";
    protected static final String SLACK_VARIABLES_LABEL_PREFIX = "s";
    protected static final String ARTIFICIAL_VARIABLES_LABEL_PREFIX = "a";
    protected static final String OBJECTIVE_FUNCTION_LABEL_PREFIX = "f";
    protected static final String RIGHT_HAND_SIDE_LABEL_PREFIX = "b";

    /**
     * Linear objective function.
     * Линейная целевая функция.
     */
    private LinearObjectiveFunction objectiveFunction;

    /**
     * Linear constraints.
     * Линейные ограничения.
     */
    private List<LinearConstraint> constraints;

    private GoalType goalType;

    /** Amount of error to accept in floating point comparisons. */
    private double epsilon;

    private Double maxArtificialValue = null;

    /** The variables each column represents */
    protected List<String> columnLabels;
    protected List<String> rowLabels;

    /**
     * Number of decision variables.
     * Количество переменных решения [x_1, x_2, ..., x_n].
     */
    protected int numDecisionVariables;

    /**
     * Number of slack variables.
     * Количество нестрогих переменных [s_1=x_(n+1), s_2=x_(n+2), ... s_m=x_(n+m)].
     */
    protected int numSlackVariables;

    /**
     * Number of artificial variables.
     * Число искусственных переменных [a_1=x_(n+m+1), ...].
     */
    protected int numArtificialVariables;

    /**
     * Является ли исходня таблица двойственно-допустимой.
     */
    protected boolean problemDualAdmissible;

    protected List<Integer> zeroIndexes;
    protected List<String> zeroColumnsLabels;
    
    protected List<ValueField[]> artificialRows;

    public void createTableau() {
        this.problemDualAdmissible = isDualAdmissible();
        this.numDecisionVariables = getOriginalNumDecisionVariables();
        this.numSlackVariables = getConstraintTypeCounts(Relationship.LEQ)
                + getConstraintTypeCounts(Relationship.GEQ);
        this.numArtificialVariables = getArtificialVariablesCount();

        initializeLabels();
        artificialRows = new ArrayList<ValueField[]>();
    }

    /**
     * @return the problemDualAdmissible
     */
    public boolean isProblemDualAdmissible() {
        return problemDualAdmissible;
    }

    /**
     * @param problemDualAdmissible the problemDualAdmissible to set
     */
    public void setProblemDualAdmissible(boolean problemDualAdmissible) {
        this.problemDualAdmissible = problemDualAdmissible;
    }

    public boolean  getProblemDualAdmissible() {
        return problemDualAdmissible;
    }

    /**
     * @return the maxArtificialValue
     */
    public Double getMaxArtificialValue() {
        return maxArtificialValue;
    }

    /**
     * @param maxArtificialValue the maxArtificialValue to set
     */
    public void setMaxArtificialValue(Double maxArtificialValue) {
        this.maxArtificialValue = maxArtificialValue;
    }

    /**
     * @return the objectiveFunction
     */
    public LinearObjectiveFunction getObjectiveFunction() {
        return objectiveFunction;
    }

    /**
     * @param objectiveFunction the objectiveFunction to set
     */
    public void setObjectiveFunction(LinearObjectiveFunction objectiveFunction) {
        this.objectiveFunction = objectiveFunction;
    }

    /**
     * @return the constraints
     */
    public List<LinearConstraint> getConstraints() {
        return constraints;
    }

    /**
     * @param constraints the constraints to set
     */
    public void setConstraints(List<LinearConstraint> constraints) {
        this.constraints = constraints;
    }

    /**
     * @return the goalType
     */
    public GoalType getGoalType() {
        return goalType;
    }

    /**
     * @param goalType the goalType to set
     */
    public void setGoalType(GoalType goalType) {
        this.goalType = goalType;
    }

    /**
     * @return the epsilon
     */
    public double getEpsilon() {
        return epsilon;
    }

    /**
     * @param epsilon the epsilon to set
     */
    public void setEpsilon(double epsilon) {
        this.epsilon = epsilon;
    }

    /**
     * Initialize the labels for the columns.
     */
    protected void initializeLabels() {
        columnLabels = new ArrayList<String>();
        rowLabels = new ArrayList<String>();

        columnLabels.add(RIGHT_HAND_SIDE_LABEL_PREFIX);
        for (int i = 0; i < getOriginalNumDecisionVariables(); i++) {
            columnLabels.add(DECISION_VARIABLES_LABEL_PREFIX + i);
        }

        if (getObjectiveFunction() != null) {
            rowLabels.add(OBJECTIVE_FUNCTION_LABEL_PREFIX);
            for (int i = 0; i < getOriginalNumDecisionVariables(); i++) {
                rowLabels.add(NON_BASE_DECISION_VARIABLES_LABEL_PREFIX + i);
            }
        }
        for (int i = 0; i < getNumSlackVariables(); i++) {
            rowLabels.add(SLACK_VARIABLES_LABEL_PREFIX + i);
        }
        for (int i = 0; i < getNumArtificialVariables(); i++) {
            rowLabels.add(ARTIFICIAL_VARIABLES_LABEL_PREFIX + i);
        }
    }

    protected int getObjectiveFunctionsOffset() {
        return 0;
    }

    protected int getRightHandSideOffset() {
        return 0;
    }

    /**
     * Get the offset of the first slack variable.
     * @return offset of the first slack variable
     */
    public int getSlackVariableOffset() {
        return getNumObjectiveFunctions() + getOriginalNumDecisionVariables();
    }

    /**
     * Get the offset of the first artificial variable.
     * @return offset of the first artificial variable
     */
    public int getArtificialVariableOffset() {
        return getNumObjectiveFunctions() + getOriginalNumDecisionVariables() + numSlackVariables;
    }

    /**
     * Get the number of objective functions in this tableau.
     * @return 1.
     */
    public int getNumObjectiveFunctions() {
        return getObjectiveFunction() != null ? 1 : 0;
    }

    public int getNumRightHandSide() {
        return 1;
    }

    /**
     * Get the number of decision variables.
     * <p>
     * If variables are not restricted to positive values, this will include 1
     * extra decision variable to represent the absolute value of the most
     * negative variable.
     * </p>
     * @return number of decision variables
     * @see #getOriginalNumDecisionVariables()
     */
    protected int getNumDecisionVariables() {
        return numDecisionVariables;
    }

    /**
     * Get the original number of decision variables.
     * @return original number of decision variables
     * @see #getNumDecisionVariables()
     */
    protected int getOriginalNumDecisionVariables() {
        return (getObjectiveFunction() != null ?
            getObjectiveFunction().getCoefficients().getDimension() :
            getConstraints().get(0).getCoefficients().getDimension());
    }

    /**
     * Get the number of slack variables.
     * @return number of slack variables
     */
    protected int getNumSlackVariables() {
        return numSlackVariables;
    }

    /**
     * Get the number of artificial variables.
     * @return number of artificial variables
     */
    protected int getNumArtificialVariables() {
        return numArtificialVariables;
    }

    protected String getLabel(final int coefficientIndex)
            throws IndexOutOfBoundsException {
        if (coefficientIndex < getOriginalNumDecisionVariables()) {
            return DECISION_VARIABLES_LABEL_PREFIX + coefficientIndex;
        }
        throw new IndexOutOfBoundsException("Index " + coefficientIndex + "must be less then " + getOriginalNumDecisionVariables());
    }

    protected boolean shouldSolutionComponentBeOne(int index) {
        int zeroColumnLabelIndex = zeroColumnsLabels.indexOf(getLabel(index));
        return (zeroColumnLabelIndex >= 0);
    }

    /**
     * Get the current solution.
     *
     * @return current solution
     */
    public RealPointValuePair getSolution() {
        double[] coefficients = new double[getOriginalNumDecisionVariables()];
        for (int i = 0; i < coefficients.length; i++) {
            if (shouldSolutionComponentBeOne(i)) {
                coefficients[i] = 1;
                continue;
            }

            int rowIndex = rowLabels.indexOf(getLabel(i));

            if (rowIndex < 0) {
                coefficients[i] = 0;
                continue;
            }
            double value = getRealEntry(rowIndex, getRightHandSideOffset());

            if (MathUtils.compareTo(value, 0, getEpsilon()) == 0) {
                coefficients[i] = 0;
                continue;
            }

            coefficients[i] = value;
        }
        return new RealPointValuePair(coefficients, getObjectiveFunction().getValue(coefficients));
    }


    @Override
    public String toString() {
        String result = "";

        if (columnLabels != null) {
            result += String.format("%1$-8s", "");
            for (String column : columnLabels) {
                result += (String.format("%1$-8s", column));
            }
            result += "\n";
        }

        for (int i = 0; i < getHeight(); i++) {
            if (rowLabels != null) {
                result += (String.format("%1$-8s", rowLabels.get(i)));
            }
            for (int j = 0; j < getWidth(); j++) {
                result += (String.format("%1$-8s", getRealEntry(i, j)));
            }
            result += "\n";
        }
        return result;
    }

    public void replaceLabels(final int rowIndex, final int colIndex) {
        String colLabel = columnLabels.get(colIndex);
        String rowLabel = rowLabels.get(rowIndex);

        columnLabels.set(colIndex, rowLabel);
        rowLabels.set(rowIndex, colLabel);
    }

    /**
     * Returns whether the problem is at an optimal state.
     * @return whether the model has been solved
     */
    public boolean isOptimal() {
        for (int i = getNumObjectiveFunctions(); i < getHeight(); i++) {
            if (MathUtils.compareTo(getRealEntry(i, 0), 0, getEpsilon()) < 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Проверяет, будет ли симплексная таблица двойственно-допустимой.
     * @return true, если симплексная таблица будет двойственно-допустимой.
     */
    protected boolean isDualAdmissible() {
        if (getObjectiveFunction() != null) {
            Iterator<RealVector.Entry> iter = getObjectiveFunction().getCoefficients().iterator();
            while (iter.hasNext()) {
                RealVector.Entry entry = iter.next();
                double value = ((getGoalType() == GoalType.MAXIMIZE) ? (-1) : (1)) * entry.getValue();
                if (value < 0) {
                    return false;
                }
            }
        }
        return true;
    }

    protected int getArtificialVariablesCount() {
        return isProblemDualAdmissible() ? 0 : 1;
    }

    /**
     * Get a count of constraints corresponding to a specified relationship.
     * @param relationship relationship to count
     * @return number of coeff with the specified relationship
     */
    protected int getConstraintTypeCounts(final Relationship relationship) {
        int count = 0;
        for (final LinearConstraint constraint : getConstraints()) {
            if (constraint.getRelationship() == relationship) {
                ++count;
            }
        }
        return count;
    }

    public Integer getPivotRow() {
        Double maxValue = null;
        Integer p = null;
        for (int i = /*getNumObjectiveFunctions()*/getSlackVariableOffset(); i < getHeight(); i++) {
            double val = getRealEntry(i, 0);
            if (MathUtils.compareTo(val, 0, getEpsilon()) < 0) {
                if (maxValue == null || Math.abs(val) > Math.abs(maxValue)) {
                    maxValue = val;
                    p = i;
                }
            }
        }
        return p;
    }

    public int appendArtificialRow(ValueField[] row) {
        int index = getHeight();
        artificialRows.add(row);
        rowLabels.add(ARTIFICIAL_VARIABLES_LABEL_PREFIX + numArtificialVariables++);
        return index;
    }
    
    public int appendArtificialRows(List<ValueField[]> rows) {
        int firstAppendedRowIndex = getHeight();
        Iterator<ValueField[]> iterator = rows.iterator();
        while (iterator.hasNext()) {
            appendArtificialRow(iterator.next());
        }
        return firstAppendedRowIndex;
    }

    public void removeArtificialRows(int startIndex, int count) {
        int startListIndex = startIndex - (getHeight() - artificialRows.size());
        for (int i = startListIndex + count - 1; i >= startListIndex; i--) {
            removeArtificialRow(i);
        }
    }

    public void removeArtificialRow(int rowIndex) {
        artificialRows.remove(getHeight() - 1 - rowIndex);
        rowLabels.remove(rowIndex);
    }
}
