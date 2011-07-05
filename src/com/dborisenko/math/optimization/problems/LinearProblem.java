/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dborisenko.math.optimization.problems;

import java.util.Collection;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import org.apache.commons.math.optimization.GoalType;
import org.apache.commons.math.optimization.linear.LinearConstraint;
import org.apache.commons.math.optimization.linear.LinearObjectiveFunction;

/**
 *
 * @author Denis
 */
@Entity
@Inheritance(strategy=InheritanceType.JOINED)
public abstract class LinearProblem
        extends Problem
        implements LinearOptimizationProblem {

    protected transient LinearObjectiveFunction objectiveFunction;
    protected transient List<LinearConstraint> constraints;

//    @Enumerated(EnumType.STRING)
    protected transient GoalType goalType;

    public LinearObjectiveFunction getObjectiveFunction() {
        return objectiveFunction;
    }
    public List<LinearConstraint> getLinearConstraints() {
        return constraints;
    }
    public GoalType getGoalType() {
        return goalType;
    }
}
