package com.dborisenko.math.optimization.linear.lexDualSimplexClasses.tableaus;

import java.util.List;
import org.apache.commons.math.FieldElement;
import org.apache.commons.math.linear.FieldMatrix;

/**
 *
 * @author Denis
 */
public class ZeroConstraintsLexDualSimplexTableau<ValueField extends FieldElement<ValueField>>
        extends LexDualSimplexTableau<ValueField> {

    @Override
    protected FieldMatrix<ValueField> getMatrixWithProcessedZeroColumns(final List<Integer> zeroIndexes,
            final FieldMatrix<ValueField> matrix,
            final ValueField zeroColumnsObjectiveSum) {

        if (zeroIndexes.size() == 0) {
            return matrix;
        }

        updateZeroObjectiveValue(matrix, zeroColumnsObjectiveSum);
        return addZeroConstraints(zeroIndexes, matrix);
    }

    protected FieldMatrix<ValueField> addZeroConstraints(final List<Integer> zeroIndexes,
            final FieldMatrix<ValueField> matrix) {
        
        FieldMatrix<ValueField> resultMatrix = createMatrix(
                matrix.getRowDimension() + zeroIndexes.size(),
                matrix.getColumnDimension());
        int resultI = 0;
        for (int i = 0; i < resultMatrix.getRowDimension(); i++) {
            if (i < matrix.getRowDimension()) {
                resultMatrix.setRow(i, matrix.getRow(i));
            } else {
                int zeroColumnIndex = (int)zeroIndexes.get(resultI);
                fillRow(resultMatrix, i, 0, getFieldHelper().getZero());
                resultMatrix.setEntry(i, getRightHandSideOffset(), getFieldHelper().getOne());
                resultMatrix.setEntry(i, zeroColumnIndex, getFieldHelper().getOne());
                rowLabels.add(ARTIFICIAL_VARIABLES_LABEL_PREFIX + numArtificialVariables++);
                resultI++;
            }
        }
        return resultMatrix;
    }

    @Override
    protected boolean shouldSolutionComponentBeOne(int index) {
        return false;
    }
}
