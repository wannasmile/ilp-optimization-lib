/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dborisenko.math.optimization.problems.solutions;

import com.dborisenko.math.optimization.Solver;
import com.dborisenko.math.optimization.Optimizer;
import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Version;

/**
 *
 * @author Denis Borisenko
 */
@Entity
public class ProblemSolution
        implements OptimizationProblemSolution, Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long problemSolutionId;

    @Version
    private Integer problemSolutionVersion;

    private Double optimalValue;

    @OneToOne(cascade=CascadeType.ALL, targetEntity=Solver.class)
    @PrimaryKeyJoinColumn
    private Optimizer optimizer;

    public Double getOptimalValue() {
        return optimalValue;
    }

    public void setOptimalValue(Double optimalValue) {
        this.optimalValue = optimalValue;
    }

    public Optimizer getOptimizer() {
        return optimizer;
    }

    public void setOptimizer(Optimizer optimizer) {
        this.optimizer = optimizer;
    }

}
