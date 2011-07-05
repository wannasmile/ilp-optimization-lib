package com.dborisenko.math.optimization.linear.integer;

import com.dborisenko.math.optimization.linear.LinearSolver;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

/**
 *
 * @author Denis Borisenko
 */
@Entity
@Inheritance(strategy=InheritanceType.JOINED)
public abstract class IntegerLinearSolver extends LinearSolver
        implements IntegerLinearOptimizer {

}
