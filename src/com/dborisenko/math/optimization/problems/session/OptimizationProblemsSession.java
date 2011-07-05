/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dborisenko.math.optimization.problems.session;

import com.dborisenko.math.optimization.Optimizer;
import com.dborisenko.math.optimization.problems.OptimizationProblem;
import com.dborisenko.math.optimization.problems.descriptors.OptimizerDescriptor;
import com.dborisenko.math.optimization.problems.generators.ProblemGenerator;
import com.dborisenko.math.optimization.problems.metadata.OptimizationProblemMetadata;
import com.dborisenko.math.optimization.problems.resolvers.OptimizationProblemResolver;
import java.util.Collection;
import java.util.Date;
import org.apache.commons.math.optimization.OptimizationException;

/**
 *
 * @author Denis
 */
public interface OptimizationProblemsSession {
    
    Collection<OptimizationProblemResolver> getResolvers();
    Date getCreationDate();

    void generateProblems(ProblemGenerator generator) throws Exception;
    void addOptimizerDescriptor(OptimizerDescriptor optimizerDescriptor)
            throws InstantiationException, IllegalAccessException;
    void addProblem(OptimizationProblem problem, OptimizationProblemMetadata metadata)
            throws Exception;

    Collection<OptimizerDescriptor> getOptimizerDescriptors();

    void setMaxIterations(int maxIterations);
    int getMaxIterations();

    void setEpsilon(double epsilon);
    double getEpsilon();
    
    void resolveAll() throws OptimizationException;

    ResolverIterator resolveIterator();
}
