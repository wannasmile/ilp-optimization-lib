/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dborisenko.math.optimization.problems.resolvers;

import com.dborisenko.math.optimization.Optimizer;
import com.dborisenko.math.optimization.linear.LinearOptimizer;
import com.dborisenko.math.optimization.problems.Problem;
import com.dborisenko.math.optimization.problems.OptimizationProblem;
import com.dborisenko.math.optimization.problems.descriptors.OptimizerDescriptor;
import com.dborisenko.math.optimization.problems.metadata.ProblemMetadata;
import com.dborisenko.math.optimization.problems.metadata.OptimizationProblemMetadata;
import com.dborisenko.math.optimization.problems.resolvers.factories.ProblemResolverFactory;
import com.dborisenko.math.optimization.problems.solutions.ProblemSolution;
import com.dborisenko.math.optimization.problems.solutions.OptimizationProblemSolution;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Version;
import org.apache.commons.math.optimization.OptimizationException;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

/**
 *
 * @author Denis Borisenko
 */
@Entity
public class ProblemResolver
        implements OptimizationProblemResolver, Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long problemResolverId;

    @Version
    private Integer problemResolverVersion;

    @OneToOne(cascade=CascadeType.ALL, targetEntity=Problem.class)
    @PrimaryKeyJoinColumn
    protected OptimizationProblem problem;

    @OneToOne(cascade=CascadeType.ALL, targetEntity=ProblemMetadata.class)
    @PrimaryKeyJoinColumn
    protected OptimizationProblemMetadata metadata;

    @OneToMany(cascade=CascadeType.ALL, targetEntity=ProblemSolution.class,
        fetch=FetchType.LAZY)
    @LazyCollection(LazyCollectionOption.EXTRA)
    @JoinTable
    protected List<OptimizationProblemSolution> solutions = new ArrayList<OptimizationProblemSolution>();;

    protected transient List<Optimizer> optimizers = new ArrayList<Optimizer>();

    public OptimizationProblem getProblem() {
        return problem;
    }

    public void setProblem(OptimizationProblem problem) {
        this.problem = problem;
    }

    public OptimizationProblemMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(OptimizationProblemMetadata metadata) {
        this.metadata = metadata;
    }

    public List<OptimizationProblemSolution> getSolutions() {
        return solutions;
    }

    public void addOptimizer(Optimizer optimizer) {
        optimizers.add(optimizer);
    }

    public void addOptimizerDescriptor(OptimizerDescriptor optimizerDescriptor)
            throws  InstantiationException, IllegalAccessException {
        Object instance = optimizerDescriptor.newInstance();
        if (instance instanceof Optimizer) {
            addOptimizer((Optimizer)instance);
        }
    }

    public void resolve()
            throws OptimizationException, IllegalArgumentException {
        if (problem == null) {
            throw new IllegalArgumentException("The problem is not set");
        }

        OptimizerIterator optIterator = optimizerIterator();
        while (optIterator.hasNext()) {
            optIterator.optimizeNext();
        }
    }

    public OptimizerIterator optimizerIterator() {
        return new OptimizerIterator(optimizers, solutions, problem);
    }

}
