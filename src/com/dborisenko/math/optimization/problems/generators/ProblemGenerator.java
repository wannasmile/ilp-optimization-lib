/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dborisenko.math.optimization.problems.generators;

import java.util.Collection;
import java.util.List;

/**
 *
 * @author Denis
 */
public interface ProblemGenerator {

    public List<ProblemAndMetadataPair> generateProblems()
            throws Exception;
    
}
