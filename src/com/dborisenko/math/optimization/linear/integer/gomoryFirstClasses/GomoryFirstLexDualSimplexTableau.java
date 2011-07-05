/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dborisenko.math.optimization.linear.integer.gomoryFirstClasses;

import com.dborisenko.math.optimization.linear.lexDualSimplexClasses.tableaus.ZeroConstraintsLexDualSimplexTableau;
import org.apache.commons.math.FieldElement;
import org.apache.commons.math.linear.ArrayFieldVector;
import org.apache.commons.math.linear.FieldVector;

/**
 *
 * @author Denis
 */
public class GomoryFirstLexDualSimplexTableau<ValueField extends FieldElement<ValueField>>
        extends ZeroConstraintsLexDualSimplexTableau<ValueField>
        implements GomoryFirstSimplexTableau<ValueField> {

    public GomoryFirstLexDualSimplexTableau() {
    }

    public int appendNegativeFractionPartOfRow(int row) {
        FieldVector<ValueField> fractions = new ArrayFieldVector<ValueField>(getWidth(), getFieldHelper().getZero());
        for (int j = 0; j < getWidth(); j++) {
            fractions.setEntry(j, getFieldHelper().getNegative(getFieldHelper().getFractionalPart(
                    getEntry(row, j))));
        }
        return appendArtificialRow(fractions.toArray());
    }
}
