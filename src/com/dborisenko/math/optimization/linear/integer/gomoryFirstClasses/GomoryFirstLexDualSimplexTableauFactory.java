/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dborisenko.math.optimization.linear.integer.gomoryFirstClasses;

import com.dborisenko.math.optimization.OptimizerPrecision;
import com.dborisenko.math.optimization.linear.lexDualSimplexClasses.factories.LexDualSimplexTableauFactory;
import com.dborisenko.math.optimization.linear.lexDualSimplexClasses.tableaus.SimplexTableau;
import com.dborisenko.math.optimization.linear.lexDualSimplexClasses.ZeroColumnsAction;
import com.dborisenko.math.utils.BigRealHelper;

/**
 *
 * @author Denis
 */
public class GomoryFirstLexDualSimplexTableauFactory
        extends LexDualSimplexTableauFactory {

    @Override
     public SimplexTableau createTableau(ZeroColumnsAction zeroColumnsAction, OptimizerPrecision precision) {
        GomoryFirstLexDualSimplexTableau tableau = new GomoryFirstLexDualSimplexTableau();
        tableau.setFieldHelper(new BigRealHelper());
        return tableau;
    }

}
