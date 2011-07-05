package com.dborisenko.math.optimization.linear.integer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.persistence.Entity;
import org.apache.commons.math.linear.ArrayRealVector;
import org.apache.commons.math.linear.OpenMapRealMatrix;
import org.apache.commons.math.linear.OpenMapRealVector;
import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.math.linear.RealVector;
import org.apache.commons.math.optimization.GoalType;
import org.apache.commons.math.optimization.OptimizationException;
import org.apache.commons.math.optimization.RealPointValuePair;
import org.apache.commons.math.optimization.linear.LinearConstraint;
import org.apache.commons.math.optimization.linear.Relationship;
import org.apache.commons.math.util.MathUtils;

/**
 *
 * @author Denis
 */
@Entity
public class DynamicProgrammingSolver extends IntegerLinearSolver {

    private Integer setOfVectorsTCount;

    protected transient List<RealVector> setOfVectorsT = new ArrayList<RealVector>();
    protected transient int k = 0;
    protected transient RealMatrix constraintsMatrixA;
    protected transient RealVector rightHandSideVectorB;
    protected transient RealVector objectiveVectorC;

    @Override
    protected void doOptimize() throws OptimizationException {
        try {
            initConstraintsMatrixA();
            initRightHandSideVectorB();
            initObjectiveVectorC();

            setOfVectorsT.add(createVector());
            k = 0;
            while (!isComplete()) {
                doIteration();
                k++;
            }

            setOfVectorsTCount = setOfVectorsT.size();
            setOptimalPoint(getOptimumValue());
        } finally {
            setOfVectorsT.clear();
        }
    }

    protected void doIteration()
            throws OptimizationException {
        incrementIterationsCounter();

        Collection<RealVector> set = sumWithVector(setOfVectorsT, createVectorEk(k));
        Iterator<RealVector> iterator = set.iterator();
        while (iterator.hasNext()) {
            RealVector vector = iterator.next();
            if (isCompetence(vector)) {
                setOfVectorsT.add(vector);
            }
        }
    }

    protected void initConstraintsMatrixA() {
        constraintsMatrixA = new OpenMapRealMatrix(getRowsCount(), getColumnsCount());

        for (int i = 0; i < constraintsMatrixA.getRowDimension(); i++) {
            LinearConstraint currentConstraint = getLinearConstraints().get(i);
            for (int j = 0; j < constraintsMatrixA.getColumnDimension(); j++) {
                constraintsMatrixA.setEntry(i, j, currentConstraint.getCoefficients().getEntry(j));
            }
        }
    }

    protected void initRightHandSideVectorB() {
        rightHandSideVectorB = new ArrayRealVector(getRowsCount());

        for (int i = 0; i < rightHandSideVectorB.getDimension(); i++) {
            LinearConstraint currentConstraint = getLinearConstraints().get(i);
            rightHandSideVectorB.setEntry(i, currentConstraint.getValue());
        }
    }

    protected void initObjectiveVectorC() {
        objectiveVectorC = getObjectiveFunction().getCoefficients();
    }

    protected boolean isComplete() {
        return !(k < getVectorSize());
    }

    protected RealVector createVector() {
        return new OpenMapRealVector(getVectorSize());
    }

    protected int getVectorSize() {
        return getObjectiveFunction().getCoefficients().getDimension();
    }

    protected int getColumnsCount() {
        return getObjectiveFunction().getCoefficients().getDimension();
    }

    protected int getRowsCount() {
        return getLinearConstraints().size();
    }

    protected RealVector createVectorEk(int k)
    {
        RealVector e = new OpenMapRealVector(getVectorSize());
        e.setEntry(k, 1);
        return e;
    }

    protected Collection<RealVector> sumWithVector(Collection<RealVector> set,
            RealVector vector) {
        List<RealVector> result = new ArrayList<RealVector>();
        Iterator<RealVector> iterator = set.iterator();
        while (iterator.hasNext()) {
            result.add(iterator.next().add(vector));
        }
        return result;
    }

    protected boolean isCompetence(RealVector vector) {
        RealMatrix v = new OpenMapRealMatrix(vector.getDimension(), 1);
        v.setColumnVector(0, vector);
        RealMatrix result = constraintsMatrixA.multiply(v);
        for (int i = 0; i < result.getRowDimension(); i++) {
            Relationship rel = getLinearConstraints().get(i).getRelationship();
            if (rel == Relationship.LEQ &&
                    MathUtils.compareTo(result.getEntry(i, 0), rightHandSideVectorB.getEntry(i), getEpsilon()) > 0) {
                return false;
            } else if (rel == Relationship.GEQ &&
                    MathUtils.compareTo(result.getEntry(i, 0), rightHandSideVectorB.getEntry(i), getEpsilon()) < 0) {
                return false;
            } else if (rel == Relationship.EQ &&
                    !MathUtils.equals(result.getEntry(i, 0), rightHandSideVectorB.getEntry(i), getEpsilon())) {
                return false;
            }
        }
        return true;
    }

    protected RealPointValuePair getOptimumValue() {
        RealVector optVector = null;
        Double optValue = null;

        Iterator<RealVector> iterator = setOfVectorsT.iterator();
        while (iterator.hasNext()) {
            RealVector vector = iterator.next();
            Double value = getObjectiveFunction().getValue(vector);
            
            if ((optValue == null && optVector == null) ||
                    (goalType == GoalType.MAXIMIZE && value > optValue) ||
                    (goalType == GoalType.MINIMIZE && value < optValue)) {
                optVector = vector;
                optValue = value;
            }
        }
        return new RealPointValuePair(optVector.getData(), optValue);
    }

    public Integer getSetOfVectorsTCount() {
        return setOfVectorsTCount;
    }
}
