/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dborisenko.math.optimization.problems.session;

import com.dborisenko.math.optimization.Optimizer;
import com.dborisenko.math.optimization.Solver;
import com.dborisenko.math.optimization.problems.OptimizationProblem;
import com.dborisenko.math.optimization.problems.descriptors.OptimizerDescriptor;
import com.dborisenko.math.optimization.problems.generators.ProblemAndMetadataPair;
import com.dborisenko.math.optimization.problems.generators.ProblemGenerator;
import com.dborisenko.math.optimization.problems.metadata.OptimizationProblemMetadata;
import com.dborisenko.math.optimization.problems.resolvers.OptimizationProblemResolver;
import com.dborisenko.math.optimization.problems.resolvers.ProblemResolver;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import org.apache.commons.math.optimization.OptimizationException;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

/**
 *
 * @author Denis
 */
@Entity
public class ProblemsSession implements OptimizationProblemsSession, Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long sessionId;

    @Version
    private Integer sessionVersion;

    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;

    @OneToMany(cascade=CascadeType.ALL, targetEntity=ProblemResolver.class,
        fetch=FetchType.LAZY)
    @LazyCollection(LazyCollectionOption.EXTRA)
    @JoinTable
    private List<OptimizationProblemResolver> resolvers =
            new ArrayList<OptimizationProblemResolver>();

    private transient List<OptimizerDescriptor> optimizerDescriptors = new ArrayList<OptimizerDescriptor>();
    private transient int maxIterations = Solver.DEFAULT_MAX_ITERATIONS;
    private transient double epsilon = Solver.DEFAULT_EPSILON;

    public ProblemsSession() {
        creationDate = new Date();
    }

    public Collection<OptimizationProblemResolver> getResolvers() {
        return resolvers;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void resolveAll() throws OptimizationException {
        ResolverIterator iterator = resolveIterator();
        while (iterator.hasNext()) {
            iterator.resolveNext();
        }
    }

    public void generateProblems(ProblemGenerator generator)
            throws Exception {

        Collection<ProblemAndMetadataPair> problems = generator.generateProblems();

        Iterator<ProblemAndMetadataPair> iterator = problems.iterator();
        while (iterator.hasNext()) {
            ProblemAndMetadataPair item = iterator.next();
            addProblem(item.getProblem(), item.getMetadata());
        }
    }

    public void addProblem(OptimizationProblem problem, OptimizationProblemMetadata metadata)
            throws Exception {
        ProblemResolver resolver = new ProblemResolver();
        resolver.setProblem(problem);
        resolver.setMetadata(metadata);
        applyAllOptimizersToResolver(resolver);

        resolvers.add(resolver);
    }

    public void addOptimizerDescriptor(OptimizerDescriptor optimizerDescriptor)
            throws InstantiationException, IllegalAccessException {
        optimizerDescriptors.add(optimizerDescriptor);
        applyOptimizerToAllResolvers(optimizerDescriptor);
    }

    public Collection<OptimizerDescriptor> getOptimizerDescriptors() {
        return optimizerDescriptors;
    }

    protected void applyOptimizerToAllResolvers(OptimizerDescriptor optimizerDescriptor)
            throws IllegalAccessException, InstantiationException {
        Iterator<OptimizationProblemResolver> iterator = resolvers.iterator();
        while (iterator.hasNext()) {
            OptimizationProblemResolver resolver = iterator.next();
            resolver.addOptimizerDescriptor(optimizerDescriptor);
        }
    }

    protected void applyAllOptimizersToResolver(OptimizationProblemResolver resolver)
            throws InstantiationException, IllegalAccessException {
        Iterator<OptimizerDescriptor> iterator = optimizerDescriptors.iterator();
        while (iterator.hasNext()) {
            resolver.addOptimizerDescriptor(iterator.next());
        }
    }

    public void setMaxIterations(int maxIterations) {
        this.maxIterations = maxIterations;
    }
    public int getMaxIterations() {
        return maxIterations;
    }

    public void setEpsilon(double epsilon) {
        this.epsilon = epsilon;
    }
    public double getEpsilon() {
        return epsilon;
    }

    public ResolverIterator resolveIterator() {
        return new ResolverIterator(resolvers);
    }
}
