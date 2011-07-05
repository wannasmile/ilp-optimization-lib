/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dborisenko.math.optimization.problems.solutions;

import com.dborisenko.math.optimization.Optimizer;

/**
 *
 * @author Denis Borisenko
 */
public interface OptimizationProblemSolution {
    public Double getOptimalValue();
    public Optimizer getOptimizer();
}
