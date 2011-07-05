/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dborisenko.math.optimization.linear.integer.lClassesExhaustionClasses;

import com.dborisenko.math.optimization.linear.lexDualSimplexClasses.tableaus.SimplexTableau;

/**
 *
 * @author Denis
 */
public interface LClassesExhaustionSimplexTableau<ValueField>
        extends SimplexTableau<ValueField> {

    public void setMinimalParticipatingVariablesColumnIndex(Integer columnIndex);
    public Integer getMinimalParticipatingVariablesColumnIndex();

    public boolean getObjectiveFunctionUsage();
    public void setObjectiveFunctionUsage(boolean useObjectiveFunction);

    public int appendConstraintToVariableColumn(int columnIndex, ValueField constraint);

    public int appendRecordRow(Double recordValue, Double delta);
    public void updateRecordRow(Double recordValue, Double delta);
}
