/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dborisenko.math.optimization.linear.integer.lClassesExhaustionClasses;

import com.dborisenko.math.optimization.linear.lexDualSimplexClasses.tableaus.RealZeroConstraintsLexDualSimplexTableau;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.math.linear.Array2DRowRealMatrix;
import org.apache.commons.math.optimization.linear.LinearConstraint;
import org.apache.commons.math.optimization.linear.LinearObjectiveFunction;

/**
 *
 * @author Denis
 */
public class LClassesExhaustionLexDualSimplexTableau
        extends RealZeroConstraintsLexDualSimplexTableau
        implements LClassesExhaustionSimplexTableau<Double> {

    protected Integer minVarColumnIndex = 0;
    protected Integer recordRowIndex;
    protected boolean useObjectiveFunction = true;
    protected boolean useDefaultArtificialBehaviour = true;

    public void setMinimalParticipatingVariablesColumnIndex(Integer columnIndex) {
        minVarColumnIndex = columnIndex;
    }

    public Integer getMinimalParticipatingVariablesColumnIndex() {
        return minVarColumnIndex;
    }

    @Override
    protected boolean initializeArtificialVariable(final Array2DRowRealMatrix matrix,
            final LinearObjectiveFunction objectiveFunction,
            final int artificialIndex) {
        
        return useDefaultArtificialBehaviour ?
            super.initializeArtificialVariable(matrix, objectiveFunction, artificialIndex) :
            false;
    }

    @Override
    public boolean isProblemDualAdmissible() {
        return useDefaultArtificialBehaviour ? super.isProblemDualAdmissible() : true;
    }

    @Override
    protected int getArtificialVariablesCount() {
        return useDefaultArtificialBehaviour ? super.getArtificialVariablesCount() : 0;
    }

//    @Override
//    protected double calcMaxArtificialValue(final LinearObjectiveFunction objectiveFunction) {
//        return -1.0;
//    }

    @Override
    public int getNumObjectiveFunctions() {
        return useObjectiveFunction ? super.getNumObjectiveFunctions() : 0;
    }

    @Override
    protected void initializeLabels() {
        super.initializeLabels();

        if (!useObjectiveFunction) {
            rowLabels.remove(OBJECTIVE_FUNCTION_LABEL_PREFIX);
        }
    }

    @Override
    protected void initializeObjectiveFunctionEntries(final Array2DRowRealMatrix matrix,
            final boolean maximize,
            final LinearObjectiveFunction objectiveFunction) {

        if (useObjectiveFunction) {
            super.initializeObjectiveFunctionEntries(matrix, maximize, objectiveFunction);
//        } else {
//            fillArray(matrix.getDataRef()[getObjectiveFunctionsOffset()], 0, 0.0);
//            matrix.setEntry(getObjectiveFunctionsOffset(), getNumRightHandSide(), -1.0);
        }
    }

    @Override
    public void setObjectiveFunction(LinearObjectiveFunction objectiveFunction) {
        if (minVarColumnIndex != null && !minVarColumnIndex.equals(0)) {
            super.setObjectiveFunction(new LinearObjectiveFunction(
                    objectiveFunction.getCoefficients().getSubVector(minVarColumnIndex,
                        objectiveFunction.getCoefficients().getDimension() - minVarColumnIndex),
                    objectiveFunction.getConstantTerm()));
        } else {
            super.setObjectiveFunction(objectiveFunction);
        }
    }

    @Override
    public void setConstraints(List<LinearConstraint> constraints) {
        if (minVarColumnIndex != null && !minVarColumnIndex.equals(0)) {
            List<LinearConstraint> newConstraints = new ArrayList<LinearConstraint>();

            Iterator<LinearConstraint> iterator = constraints.iterator();
            while (iterator.hasNext()) {
                LinearConstraint currConstr = iterator.next();
                newConstraints.add(new LinearConstraint(currConstr.getCoefficients().getSubVector(
                            minVarColumnIndex, currConstr.getCoefficients().getDimension() - minVarColumnIndex),
                            currConstr.getRelationship(),
                            currConstr.getValue()));
            }
            super.setConstraints(newConstraints);
        } else {
            super.setConstraints(constraints);
        }
    }

    public int appendRecordRow(Double recordValue, Double delta) {
        if (recordValue == null || delta == null) {
            return -1;
        }
        Double[] recordRow = new Double[getWidth()];
        recordRowIndex = appendArtificialRow(recordRow);
        fillRecordRow(recordValue, delta);
        return recordRowIndex;
    }

    public void updateRecordRow(Double recordValue, Double delta) {
        if (recordValue == null || delta == null) {
            return;
        }
        if (recordRowIndex == null) {
            appendRecordRow(recordValue, delta);
        } else {
            fillRecordRow(recordValue, delta);
        }
    }

    protected void fillRecordRow(Double recordValue, Double delta) {
        for (int j = 0; j < getWidth(); j++) {
            if (j < getNumRightHandSide()) {
                setEntry(recordRowIndex, j, -(recordValue + delta));
            } else {
                setEntry(recordRowIndex, j,
                        -getObjectiveFunction().getCoefficients().getEntry(j - getNumRightHandSide()));
            }
        }
    }

    public int appendConstraintToVariableColumn(int variableIndex, Double constraint) {
        Double[] row = new Double[getWidth()];
        Arrays.fill(row, 0.0);
        row[variableIndex + getNumRightHandSide()] = 1.0;
        row[getRightHandSideOffset()] = constraint;
        return appendArtificialRow(row);
    }

    public boolean getObjectiveFunctionUsage() {
        return useObjectiveFunction;
    }

    public void setObjectiveFunctionUsage(boolean useObjectiveFunction) {
        this.useObjectiveFunction = useObjectiveFunction;
    }

    /**
     * @return the useDefaultArtificialBehaviour
     */
    public boolean isUseDefaultArtificialBehaviour() {
        return useDefaultArtificialBehaviour;
    }

    /**
     * @param useDefaultArtificialBehaviour the useDefaultArtificialBehaviour to set
     */
    public void setUseDefaultArtificialBehaviour(boolean useDefaultArtificialBehaviour) {
        this.useDefaultArtificialBehaviour = useDefaultArtificialBehaviour;
    }
}
