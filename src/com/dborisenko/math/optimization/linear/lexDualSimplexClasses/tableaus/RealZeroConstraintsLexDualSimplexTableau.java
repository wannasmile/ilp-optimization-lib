/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dborisenko.math.optimization.linear.lexDualSimplexClasses.tableaus;

import java.util.List;
import org.apache.commons.math.linear.Array2DRowRealMatrix;

/**
 *
 * @author Denis
 */
public class RealZeroConstraintsLexDualSimplexTableau
        extends RealLexDualSimplexTableau {

    @Override
    protected Array2DRowRealMatrix getMatrixWithProcessedZeroColumns(final List<Integer> zeroIndexes,
            final Array2DRowRealMatrix matrix,
            final double zeroColumnsObjectiveSum) {

        if (zeroIndexes.isEmpty()) {
            return matrix;
        }

        updateZeroObjectiveValue(matrix, zeroColumnsObjectiveSum);
        return addZeroConstraints(zeroIndexes, matrix);
    }

    protected Array2DRowRealMatrix addZeroConstraints(final List<Integer> zeroIndexes,
            final Array2DRowRealMatrix matrix) {

        Array2DRowRealMatrix resultMatrix = new Array2DRowRealMatrix(matrix.getRowDimension() + zeroIndexes.size(),
                matrix.getColumnDimension());
        int resultI = 0;
        for (int i = 0; i < resultMatrix.getRowDimension(); i++) {
            if (i < matrix.getRowDimension()) {
                resultMatrix.setRow(i, matrix.getRow(i));
            } else {
                int zeroColumnIndex = (int)zeroIndexes.get(resultI++);
                double[] row = new double[matrix.getColumnDimension()];
                row[getRightHandSideOffset()] = 1.0;
                row[zeroColumnIndex] = 1.0;
                resultMatrix.setRow(i, row);
                rowLabels.add(ARTIFICIAL_VARIABLES_LABEL_PREFIX + numArtificialVariables++);
            }
        }
        return resultMatrix;
    }

    @Override
    protected boolean shouldSolutionComponentBeOne(int index) {
        return false;
    }
}