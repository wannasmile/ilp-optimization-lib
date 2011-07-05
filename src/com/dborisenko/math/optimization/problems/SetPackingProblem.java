package com.dborisenko.math.optimization.problems;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.Entity;
import org.apache.commons.math.linear.ArrayRealVector;
import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.math.linear.RealVector;
import org.apache.commons.math.optimization.GoalType;
import org.apache.commons.math.optimization.linear.LinearConstraint;
import org.apache.commons.math.optimization.linear.LinearObjectiveFunction;
import org.apache.commons.math.optimization.linear.Relationship;

/**
 *
 * @author Denis Borisenko
 */
@Entity
public class SetPackingProblem extends LinearProblem {

    private static final long serialVersionUID = 1L;
    
    protected static final double OBJECTIVE_FUNCTION_CONSTANT_TERM = 0.0;
    protected static final Relationship CONSTRAINT_RELATIONSHIP = Relationship.LEQ;
    protected static final double CONSTRAINT_VALUE = 1.0;
    protected static final GoalType GOAL_TYPE = GoalType.MAXIMIZE;
    protected static final double UNWEIGHTED_WEIGHT_VALUE = 1.0;
    public static final double CONSTRAINT_COEFFICIENT_VALUE = 1.0;

    protected Integer variablesCountN;
    
    protected Integer constraintsCountM;

    protected SetPackingProblem() {
        this(0, 0);
    }

    public SetPackingProblem(final RealMatrix constraintsMatrixA) {
        this(constraintsMatrixA.getColumnDimension(), 
                constraintsMatrixA.getRowDimension());
        objectiveFunction = createUnweightedObjectiveFunction();
        constraints = createLinearConstraints(constraintsMatrixA);
    }

    /**
     * Unweighted set packing problem
     * Невзвешенная задача об упаковке.
     */
    public SetPackingProblem(LinearObjectiveFunction objectiveFunction,
            List<LinearConstraint> constraints) {
        this(objectiveFunction.getCoefficients().getDimension(),
                constraints.size());
        this.objectiveFunction = objectiveFunction;
        this.constraints = constraints;
    }

    /**
     * Unweighted set packing problem
     * Невзвешенная задача об упаковке.
     */
    public SetPackingProblem(final double[][] constraintsMatrixA) {
        this(constraintsMatrixA[0].length, constraintsMatrixA.length);
        objectiveFunction = createUnweightedObjectiveFunction();
        constraints = createLinearConstraints(constraintsMatrixA);
    }

    public SetPackingProblem(final RealVector weightsC, 
            final RealMatrix constraintsMatrixA) {

        this(weightsC.getData(), constraintsMatrixA.getData());
    }

    public SetPackingProblem(final double[] weightsC, 
            final double[][] constraintsMatrixA) {

        this(weightsC.length, constraintsMatrixA.length);
        objectiveFunction = createObjectiveFunction(weightsC);
        constraints = createLinearConstraints(constraintsMatrixA);
    }

    public SetPackingProblem(final int variablesCountN, 
            final int constraintsCountM) {
        
        this.variablesCountN = variablesCountN;
        this.constraintsCountM = constraintsCountM;
        this.goalType = GOAL_TYPE;
    }

    protected LinearObjectiveFunction createUnweightedObjectiveFunction() {
        RealVector weight = new ArrayRealVector(variablesCountN,
                UNWEIGHTED_WEIGHT_VALUE);
        return new LinearObjectiveFunction(weight, 
                OBJECTIVE_FUNCTION_CONSTANT_TERM);
    }

    protected LinearObjectiveFunction createObjectiveFunction(final double[] weights) {
        return new LinearObjectiveFunction(weights, OBJECTIVE_FUNCTION_CONSTANT_TERM);
    }

    protected LinearObjectiveFunction createObjectiveFunction(RealVector weights) {
        return new LinearObjectiveFunction(weights, OBJECTIVE_FUNCTION_CONSTANT_TERM);
    }

    protected List<LinearConstraint> createLinearConstraints(final double[][] constraintsMatrix) {
        List<LinearConstraint> constraintsList = new ArrayList<LinearConstraint>();
        for (int i = 0; i < constraintsMatrix.length; i++) {
            constraintsList.add(new LinearConstraint(constraintsMatrix[i],
                    CONSTRAINT_RELATIONSHIP, CONSTRAINT_VALUE));
        }
        return constraintsList;
    }

    protected List<LinearConstraint> createLinearConstraints(RealMatrix constraintsMatrix) {
        List<LinearConstraint> constraintsList = new ArrayList<LinearConstraint>();
        for (int i = 0; i < constraintsMatrix.getRowDimension(); i++) {
            constraintsList.add(new LinearConstraint(constraintsMatrix.getRow(i),
                    CONSTRAINT_RELATIONSHIP, CONSTRAINT_VALUE));
        }
        return constraintsList;
    }

    public int getVariablesCountN() {
        return variablesCountN;
    }

    public int getConstraintsCountM() {
        return constraintsCountM;
    }
}
