package com.dborisenko.math.optimization;

/**
 *
 * @author Denis Borisenko
 */
public enum OptimizerStatus {
    NOT_PROCEED_TO_SOLVING,
    IN_PROGRESS,
    SOLVED,
    MAX_ITERATIONS_EXCEEDED,
    ERROR,
    UNBOUNDED_SOLUTION
}
