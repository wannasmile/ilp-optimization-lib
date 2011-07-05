/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dborisenko.math.optimization.problems.generators;

import com.dborisenko.math.optimization.problems.LinearOptimizationProblem;
import com.dborisenko.math.optimization.problems.metadata.OptimizationProblemMetadata;

/**
 *
 * @author Denis
 */
public class ProblemAndMetadataPair {
    private LinearOptimizationProblem problem;
    private OptimizationProblemMetadata metadata;

    public ProblemAndMetadataPair(LinearOptimizationProblem problem) {
        this.problem = problem;
    }

    public ProblemAndMetadataPair(LinearOptimizationProblem problem,
            OptimizationProblemMetadata metadata) {
        this.problem = problem;
        this.metadata = metadata;
    }

    /**
     * @return the problem
     */
    public LinearOptimizationProblem getProblem() {
        return problem;
    }

    /**
     * @param problem the problem to set
     */
    public void setProblem(LinearOptimizationProblem problem) {
        this.problem = problem;
    }

    /**
     * @return the metadata
     */
    public OptimizationProblemMetadata getMetadata() {
        return metadata;
    }

    /**
     * @param metadata the metadata to set
     */
    public void setMetadata(OptimizationProblemMetadata metadata) {
        this.metadata = metadata;
    }
}
