/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dborisenko.math.optimization.problems;

import java.util.Collection;
import java.util.List;
import org.apache.commons.math.optimization.GoalType;
import org.apache.commons.math.optimization.linear.LinearConstraint;
import org.apache.commons.math.optimization.linear.LinearObjectiveFunction;

/**
 *
 * @author Denis
 */
public interface LinearOptimizationProblem
        extends OptimizationProblem {

    LinearObjectiveFunction getObjectiveFunction();
    List<LinearConstraint> getLinearConstraints();
    GoalType getGoalType();
}
