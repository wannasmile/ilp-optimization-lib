/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dborisenko.math.optimization;

import java.util.Date;
import org.apache.commons.math.optimization.OptimizationException;

/**
 *
 * @author Denis
 */
public interface Optimizer {

    void setMaxIterations(int maxIterations);
    int getMaxIterations();

    void setEpsilon(double epsilon);
    double getEpsilon();

    int getIterations();

    Date getStartTime();
    Date getStopTime();

    OptimizerStatus getStatus();
    String getErrorMessage();

    void optimize() throws OptimizationException;
}
