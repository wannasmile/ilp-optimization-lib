package com.dborisenko.math.optimization.problems.resolvers.factories;

import com.dborisenko.math.optimization.Optimizer;
import com.dborisenko.math.optimization.linear.LinearOptimizer;
import com.dborisenko.math.optimization.problems.LinearOptimizationProblem;
import com.dborisenko.math.optimization.problems.OptimizationProblem;

/**
 *
 * @author Denis Borisenko
 */
public class ProblemResolverFactory {

    public static void initOptimizer(OptimizationProblem problem,
            Optimizer optimizer) {
        if (problem instanceof LinearOptimizationProblem
                && optimizer instanceof LinearOptimizer) {
            initLinearOptimizer((LinearOptimizationProblem)problem,
                    (LinearOptimizer)optimizer);
        }
    }

    public static void initLinearOptimizer(LinearOptimizationProblem problem,
            LinearOptimizer optimizer) {
        optimizer.setGoalType(problem.getGoalType());
        optimizer.setLinearConstraints(problem.getLinearConstraints());
        optimizer.setObjectiveFunction(problem.getObjectiveFunction());
    }
}
