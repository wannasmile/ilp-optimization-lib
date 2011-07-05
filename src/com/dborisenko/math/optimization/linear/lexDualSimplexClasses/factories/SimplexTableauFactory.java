/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dborisenko.math.optimization.linear.lexDualSimplexClasses.factories;

import com.dborisenko.math.optimization.linear.lexDualSimplexClasses.tableaus.SimplexTableau;
import com.dborisenko.math.optimization.OptimizerPrecision;
import com.dborisenko.math.optimization.linear.lexDualSimplexClasses.ZeroColumnsAction;

/**
 *
 * @author Denis
 */
public interface SimplexTableauFactory {
    public SimplexTableau createTableau(ZeroColumnsAction zeroColumnsAction,
            OptimizerPrecision precision);
}
