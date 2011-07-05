/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dborisenko.math.optimization.linear.lexDualSimplexClasses.factories;

import com.dborisenko.math.optimization.linear.lexDualSimplexClasses.tableaus.ZeroConstraintsLexDualSimplexTableau;
import com.dborisenko.math.optimization.linear.lexDualSimplexClasses.tableaus.LexDualSimplexTableau;
import com.dborisenko.math.optimization.linear.lexDualSimplexClasses.tableaus.RealLexDualSimplexTableau;
import com.dborisenko.math.optimization.linear.lexDualSimplexClasses.tableaus.RealZeroConstraintsLexDualSimplexTableau;
import com.dborisenko.math.optimization.linear.lexDualSimplexClasses.tableaus.SimplexTableau;
import com.dborisenko.math.optimization.OptimizerPrecision;
import com.dborisenko.math.optimization.linear.lexDualSimplexClasses.ZeroColumnsAction;
import com.dborisenko.math.utils.BigRealHelper;
import org.apache.commons.math.util.BigReal;

/**
 *
 * @author Denis
 */
public class LexDualSimplexTableauFactory implements SimplexTableauFactory {

    public SimplexTableau createTableau(ZeroColumnsAction zeroColumnsAction, OptimizerPrecision precision) {
        SimplexTableau result = null;
        if (precision == OptimizerPrecision.BIG) {
            if (zeroColumnsAction == ZeroColumnsAction.REMOVE_COLUMNS) {
                result = new LexDualSimplexTableau<BigReal>();
            } else if (zeroColumnsAction == ZeroColumnsAction.ADD_CONSTRAINTS) {
                result = new ZeroConstraintsLexDualSimplexTableau<BigReal>();
            }
            ((LexDualSimplexTableau<BigReal>)result).setFieldHelper(new BigRealHelper());
        } else if (precision == OptimizerPrecision.DOUBLE) {
            if (zeroColumnsAction == ZeroColumnsAction.REMOVE_COLUMNS) {
                result = new RealLexDualSimplexTableau();
            } else if (zeroColumnsAction == ZeroColumnsAction.ADD_CONSTRAINTS) {
                result = new RealZeroConstraintsLexDualSimplexTableau();
            }
        }

        return result;
    }

}
