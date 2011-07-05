/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dborisenko.math.optimization.linear.integer.lClassesExhaustionClasses;

import com.dborisenko.math.optimization.linear.lexDualSimplexClasses.tableaus.RealZeroConstraintsLexDualSimplexTableau;
import java.util.Arrays;
import org.apache.commons.math.optimization.linear.LinearObjectiveFunction;

/**
 *
 * @author Denis
 */
public class LClassesExhaustionRealLexDualSimplexTableau
        extends RealZeroConstraintsLexDualSimplexTableau
        implements LClassesExhaustionSimplexTableau<Double> {

    protected int minVarColumnIndex = 0;
    protected Integer recordRowIndex;

    public void setMinimalParticipatingVariablesColumnIndex(Integer columnIndex) {
        minVarColumnIndex = columnIndex;
        updateMaxArtificialValue();
    }

    protected void updateMaxArtificialValue() {
        if (!isProblemDualAdmissible()) {
            double sumVal = 0.0;
            for (int i = minVarColumnIndex; i < getObjectiveFunction().getCoefficients().getDimension(); i++) {
                sumVal += getObjectiveFunction().getCoefficients().getEntry(i);
            }
            setEntry(getArtificialVariableOffset(),
                    getRightHandSideOffset(),
                    sumVal);
        }
    }

    public Integer getMinimalParticipatingVariablesColumnIndex() {
        return minVarColumnIndex;
    }

    protected int getSourceWidth() {
        return super.getWidth();
    }

    @Override
    public int getWidth() {
        return getSourceWidth() - minVarColumnIndex;
    }

    @Override
    public Double getEntry(final int row, final int column) {
        return super.getEntry(row,
                column < getNumRightHandSide() ?
                    column :
                    column + minVarColumnIndex);
    }

    @Override
    public void setEntry(final int row, final int column,
            final Double value) {
        super.setEntry(row,
                column < getNumRightHandSide() ?
                    column :
                    column + minVarColumnIndex,
                    value);
    }

    @Override
    public void replaceLabels(final int rowIndex, final int colIndex) {
        super.replaceLabels(rowIndex, colIndex + minVarColumnIndex);
    }

    public int appendRecordRow(Double recordValue, Double delta) {
        if (recordValue == null || delta == null) {
            return -1;
        }
        Double[] recordRow = new Double[getSourceWidth()];
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
        for (int j = 0; j < getSourceWidth(); j++) {
            if (j < getNumRightHandSide()) {
                super.setEntry(recordRowIndex, j, -(recordValue + delta));
            } else {
                super.setEntry(recordRowIndex, j,
                        -getObjectiveFunction().getCoefficients().getEntry(j - getNumRightHandSide()));
            }
        }
    }

    public int appendConstraintToVariableColumn(int variableIndex, Double constraint) {
        Double[] row = new Double[getSourceWidth()];
        Arrays.fill(row, 0.0);
        row[variableIndex + getNumRightHandSide()] = 1.0;
        row[getRightHandSideOffset()] = constraint;
        return appendArtificialRow(row);
    }

    public boolean getObjectiveFunctionUsage() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setObjectiveFunctionUsage(boolean useObjectiveFunction) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
