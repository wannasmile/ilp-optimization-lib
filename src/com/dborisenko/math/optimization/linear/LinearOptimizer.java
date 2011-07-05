/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dborisenko.math.optimization.linear;

import com.dborisenko.math.optimization.Optimizer;
import java.util.Collection;
import java.util.List;
import org.apache.commons.math.optimization.GoalType;
import org.apache.commons.math.optimization.RealPointValuePair;
import org.apache.commons.math.optimization.linear.LinearConstraint;
import org.apache.commons.math.optimization.linear.LinearObjectiveFunction;

/**
 *
 * @author Denis Borisenko
 */
public interface LinearOptimizer extends Optimizer {
    void setObjectiveFunction(LinearObjectiveFunction f);
    LinearObjectiveFunction getObjectiveFunction();

    void setLinearConstraints(List<LinearConstraint> constraints);
    List<LinearConstraint> getLinearConstraints();

    void setGoalType(GoalType goalType);
    GoalType getGoalType();

    RealPointValuePair getOptimalPoint();

    Double getOptimalValue();
}
