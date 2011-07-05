package com.dborisenko.math.optimization.problems.resolvers;

import com.dborisenko.math.optimization.Optimizer;
import com.dborisenko.math.optimization.linear.LinearOptimizer;
import com.dborisenko.math.optimization.problems.OptimizationProblem;
import com.dborisenko.math.optimization.problems.resolvers.factories.ProblemResolverFactory;
import com.dborisenko.math.optimization.problems.solutions.OptimizationProblemSolution;
import com.dborisenko.math.optimization.problems.solutions.ProblemSolution;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.math.optimization.OptimizationException;

/**
 *
 * @author Denis Borisenko
 */
public class OptimizerIterator implements Iterator<Optimizer> {

    private final Iterator<Optimizer> iterator;
    private final List<OptimizationProblemSolution> solutions;
    private final OptimizationProblem problem;

    public OptimizerIterator(Collection<Optimizer> optimizers,
            List<OptimizationProblemSolution> solutions,
            OptimizationProblem problem) {
        this.iterator = optimizers.iterator();
        this.solutions = solutions;
        this.problem = problem;
    }

    public boolean hasNext() {
        return iterator.hasNext();
    }

    public Optimizer next() {
        return iterator.next();
    }

    public void remove() {
        iterator.remove();
    }

    public Optimizer optimizeNext() 
            throws OptimizationException {
        Optimizer optimizer = iterator.next();
        optimize(optimizer);
        return optimizer;
    }

    private void optimize(Optimizer optimizer)
            throws OptimizationException, IllegalArgumentException {
        ProblemResolverFactory.initOptimizer(problem, optimizer);

        ProblemSolution solution = new ProblemSolution();
        solution.setOptimizer(optimizer);
        solutions.add(solution);
        
        optimizer.optimize();

        if (optimizer instanceof LinearOptimizer) {
            solution.setOptimalValue(((LinearOptimizer)optimizer).getOptimalValue());
        }
    }

}
