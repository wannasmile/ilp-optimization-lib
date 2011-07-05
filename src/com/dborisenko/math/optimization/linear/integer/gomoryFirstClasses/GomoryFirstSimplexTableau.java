/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dborisenko.math.optimization.linear.integer.gomoryFirstClasses;

import com.dborisenko.math.optimization.linear.lexDualSimplexClasses.tableaus.SimplexTableau;
import org.apache.commons.math.FieldElement;

/**
 *
 * @author Denis
 */
public interface GomoryFirstSimplexTableau<ValueField extends FieldElement<ValueField>>
        extends SimplexTableau<ValueField> {

    public int appendNegativeFractionPartOfRow(int row);
}
