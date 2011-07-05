/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dborisenko.math.optimization.linear.integer.lClassesExhaustionClasses;

import com.dborisenko.math.optimization.OptimizerPrecision;
import com.dborisenko.math.optimization.linear.lexDualSimplexClasses.ZeroColumnsAction;
import com.dborisenko.math.optimization.linear.lexDualSimplexClasses.factories.SimplexTableauFactory;
import com.dborisenko.math.optimization.linear.lexDualSimplexClasses.tableaus.SimplexTableau;
import com.dborisenko.math.utils.BigRealHelper;

/**
 *
 * @author Denis
 */
public class LClassesExhaustionLexDualSimplexTableauFactory
        implements SimplexTableauFactory {

    protected final Integer minimalParticipatingVariablesColumnIndex;
    protected final boolean useObjectiveFunction;
    protected final boolean useDefaultArtificialBehaviour;

    public LClassesExhaustionLexDualSimplexTableauFactory(Integer minimalParticipatingVariablesColumnIndex,
            boolean useObjectiveFunction, boolean useDefaultArtificialBehaviour) {
        this.minimalParticipatingVariablesColumnIndex = minimalParticipatingVariablesColumnIndex;
        this.useObjectiveFunction = useObjectiveFunction;
        this.useDefaultArtificialBehaviour = useDefaultArtificialBehaviour;
    }

    public SimplexTableau createTableau(ZeroColumnsAction zeroColumnsAction, OptimizerPrecision precision) {
//        LClassesExhaustionBigLexDualSimplexTableau<BigReal> result = new LClassesExhaustionBigLexDualSimplexTableau<BigReal>();
//        result.setFieldHelper(new BigRealHelper());
//        return result;
//        return new LClassesExhaustionRealLexDualSimplexTableau();
        LClassesExhaustionLexDualSimplexTableau result = new LClassesExhaustionLexDualSimplexTableau();
//        result.setFieldHelper(new BigRealHelper());
        result.setMinimalParticipatingVariablesColumnIndex(minimalParticipatingVariablesColumnIndex);
        result.setObjectiveFunctionUsage(useObjectiveFunction);
        result.setUseDefaultArtificialBehaviour(useDefaultArtificialBehaviour);
        return result;
    }

}
