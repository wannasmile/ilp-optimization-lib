/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dborisenko.math.optimization.linear.lexDualSimplexClasses.tableaus;

import java.util.List;
import org.apache.commons.math.optimization.GoalType;
import org.apache.commons.math.optimization.RealPointValuePair;
import org.apache.commons.math.optimization.linear.LinearConstraint;
import org.apache.commons.math.optimization.linear.LinearObjectiveFunction;

/**
 *
 * @author Denis
 */
public interface SimplexTableau<ValueField> {
    public LinearObjectiveFunction getObjectiveFunction();
    public void setObjectiveFunction(LinearObjectiveFunction objectiveFunction);

    public List<LinearConstraint> getConstraints();
    public void setConstraints(List<LinearConstraint> constraints);

    public GoalType getGoalType();
    public void setGoalType(GoalType goalType);

    public double getEpsilon();
    public void setEpsilon(double epsilon);

    public Double getMaxArtificialValue();
    public void setMaxArtificialValue(Double maxArtificialValue);

    public boolean isProblemDualAdmissible();
    public void setProblemDualAdmissible(boolean problemDualAdmissible);

    public int getArtificialVariableOffset();
    public int getNumObjectiveFunctions();
    public int getNumRightHandSide();

    public int getHeight();
    public int getWidth();

    public double getRealEntry(final int row, final int column);
    public ValueField getEntry(final int row, final int column);
    public void setEntry(final int row, final int column, final ValueField value);

    public void createTableau();
    public boolean isOptimal();
    public void doSimplexIteration(int p, int q);
    public RealPointValuePair getSolution();
    public void replaceLabels(final int rowIndex, final int colIndex);

    public Integer getPivotRow();
    public Integer getPivotColumn(Integer row, boolean ignoreNegotiveCondition);

//    public SimplexTableau createTableauWithAppendedArtificialRow(double[] artificialRow);

    public int appendArtificialRow(ValueField[] row);
    public void removeArtificialRow(int rowIndex);

    public int appendArtificialRows(List<ValueField[]> rows);
    public void removeArtificialRows(int startIndex, int count);
}
