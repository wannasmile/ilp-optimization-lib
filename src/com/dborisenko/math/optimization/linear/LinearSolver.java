/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dborisenko.math.optimization.linear;

import com.dborisenko.math.optimization.Solver;
import com.dborisenko.math.optimization.OptimizerStatus;
import java.util.Collection;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import org.apache.commons.math.optimization.GoalType;
import org.apache.commons.math.optimization.RealPointValuePair;
import org.apache.commons.math.optimization.linear.LinearConstraint;
import org.apache.commons.math.optimization.linear.LinearObjectiveFunction;

/**
 *
 * @author Denis Borisenko
 */
@Entity
@Inheritance(strategy=InheritanceType.JOINED)
public abstract class LinearSolver
        extends Solver implements LinearOptimizer {

    protected transient LinearObjectiveFunction function;
    protected transient List<LinearConstraint> linearConstraints;

    protected transient GoalType goalType;
    
    private transient RealPointValuePair optimalPoint;

    private Double optimalValue;

    public void setObjectiveFunction(LinearObjectiveFunction f) {
        this.function = f;
    }

    public LinearObjectiveFunction getObjectiveFunction() {
        return this.function;
    }

    public void setLinearConstraints(List<LinearConstraint> constraints) {
        this.linearConstraints = constraints;
    }

    public List<LinearConstraint> getLinearConstraints() {
        return this.linearConstraints;
    }

    public void setGoalType(GoalType goalType) {
        this.goalType = goalType;
    }

    public GoalType getGoalType() {
        return this.goalType;
    }

    public RealPointValuePair getOptimalPoint() {
        if (getStatus() == OptimizerStatus.SOLVED) {
            return optimalPoint;
        }
        return null;
    }

    public Double getOptimalValue() {
        return optimalValue;
    }

    protected void setOptimalPoint(RealPointValuePair optimalPoint) {
        this.optimalPoint = optimalPoint;
        this.optimalValue = (optimalPoint != null ? optimalPoint.getValue() : null);
    }

}
