package com.dborisenko.math.optimization.problems;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Version;

/**
 *
 * @author Denis Borisenko
 */
@Entity
@Inheritance(strategy=InheritanceType.JOINED)
public abstract class Problem
        implements OptimizationProblem, Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long problemId;

    @Version
    private Integer problemVersion;
}
