package com.dborisenko.math.optimization.problems.resolvers;

import com.dborisenko.math.optimization.Optimizer;
import com.dborisenko.math.optimization.problems.OptimizationProblem;
import com.dborisenko.math.optimization.problems.descriptors.OptimizerDescriptor;
import com.dborisenko.math.optimization.problems.metadata.OptimizationProblemMetadata;
import com.dborisenko.math.optimization.problems.solutions.OptimizationProblemSolution;
import java.util.Collection;
import java.util.List;
import org.apache.commons.math.optimization.OptimizationException;

/**
 *
 * @author Denis Borisenko
 */
public interface OptimizationProblemResolver {
    OptimizationProblem getProblem();
    void setProblem(OptimizationProblem problem);

    OptimizationProblemMetadata getMetadata();
    void setMetadata(OptimizationProblemMetadata metadata);
    
    void addOptimizer(Optimizer optimizer);
    void addOptimizerDescriptor(OptimizerDescriptor optimizerDescriptor)
            throws InstantiationException, IllegalAccessException;

    List<OptimizationProblemSolution> getSolutions();

    void resolve() throws OptimizationException;

    OptimizerIterator optimizerIterator();
}
