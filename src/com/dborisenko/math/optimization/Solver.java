/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dborisenko.math.optimization;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import org.apache.commons.math.MaxIterationsExceededException;
import org.apache.commons.math.optimization.OptimizationException;
import org.apache.commons.math.optimization.linear.UnboundedSolutionException;

/**
 *
 * @author Denis Borisenko
 */
@Entity
@Inheritance(strategy=InheritanceType.JOINED)
public abstract class Solver
        implements Optimizer, Serializable {

    private static final long serialVersionUID = 1L;

    public static final double DEFAULT_EPSILON = 1.0e-6;
    public static final int DEFAULT_MAX_ITERATIONS = Integer.MAX_VALUE;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long solverId;

    @Version
    private Integer solverVersion;

    protected Double epsilon = DEFAULT_EPSILON;

    protected Integer iterations;

    @Temporal(TemporalType.TIMESTAMP)
    protected Date startTime;

    @Temporal(TemporalType.TIMESTAMP)
    protected Date stopTime;

    @Enumerated(EnumType.STRING)
    protected OptimizerStatus status = OptimizerStatus.NOT_PROCEED_TO_SOLVING;

    protected String errorMessage;
    
    protected transient int maxIterations = DEFAULT_MAX_ITERATIONS;

    public void setMaxIterations(int maxIterations) {
        this.maxIterations = maxIterations;
    }

    public int getMaxIterations() {
        return this.maxIterations;
    }

    public int getIterations() {
        return this.iterations;
    }

    public void setEpsilon(double epsilon) {
        this.epsilon = epsilon;
    }

    public double getEpsilon() {
        return this.epsilon;
    }
    
    public Date getStartTime() {
        return this.startTime;
    }

    public Date getStopTime() {
        return this.stopTime;
    }

    public long getTime() {
        Date start = (getStartTime() != null ? getStartTime() : new Date());
        Date stop = (getStopTime() != null ? getStopTime() : new Date());
        return stop.getTime() - start.getTime();
    }

    public OptimizerStatus getStatus() {
        return this.status;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    protected void incrementIterationsCounter()
        throws OptimizationException {
        if (++iterations > maxIterations) {
            throw new OptimizationException(new MaxIterationsExceededException(maxIterations));
        }
    }

    public void optimize() throws OptimizationException {
        iterations = 0;
        status = OptimizerStatus.IN_PROGRESS;
        startTime = new Date();
        try {
            doOptimize();
            status = OptimizerStatus.SOLVED;
            stopTime = new Date();
        } catch (OptimizationException ex) {
            if (ex instanceof UnboundedSolutionException) {
                status = OptimizerStatus.UNBOUNDED_SOLUTION;
            } else if (ex.getCause() instanceof MaxIterationsExceededException) {
                status = OptimizerStatus.MAX_ITERATIONS_EXCEEDED;
            } else {
                status = OptimizerStatus.ERROR;
            }
            errorMessage = ex.getMessage();
            stopTime = new Date();
            throw ex;
        } catch (Throwable ex) {
            status = OptimizerStatus.ERROR;
            errorMessage = ex.getMessage();
            throw new OptimizationException(ex);
        }
    }

    protected abstract void doOptimize()
            throws OptimizationException;
}
