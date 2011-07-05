/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dborisenko.math.optimization.linear.integer.lClassesExhaustionClasses;

import org.apache.commons.math.optimization.RealPointValuePair;

/**
 *
 * @author Denis
 */
public class StepUpDownResult {
    private final RealPointValuePair solution;
    private final StepUpDownNextAction nextAction;

    public StepUpDownResult(RealPointValuePair solution, StepUpDownNextAction nextAction) {
        this.solution = solution;
        this.nextAction = nextAction;
    }

    public StepUpDownResult(StepUpDownNextAction nextAction) {
        this(null, nextAction);
    }

    public StepUpDownResult(RealPointValuePair solution) {
        this(solution, StepUpDownNextAction.INITIALIZATION);
    }

    public RealPointValuePair getSolution() {
        return solution;
    }

    public StepUpDownNextAction getNextAction() {
        return nextAction;
    }
}
