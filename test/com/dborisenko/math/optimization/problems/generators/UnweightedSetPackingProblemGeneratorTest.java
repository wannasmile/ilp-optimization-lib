/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dborisenko.math.optimization.problems.generators;

import com.dborisenko.math.optimization.problems.SetPackingProblem;
import com.dborisenko.math.optimization.problems.metadata.SetPackingProblemMetadata;
import java.util.Collection;
import java.util.Iterator;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Denis
 */
public class UnweightedSetPackingProblemGeneratorTest {

    public UnweightedSetPackingProblemGeneratorTest() {
    }

    @Test
    public void test1() throws Exception {
        UnweightedSetPackingProblemGenerator gen = new UnweightedSetPackingProblemGenerator();
        gen.getVariablesCountN().setRange(10, 1, 15);
        gen.getConstraintsCountM().setValueEval("2*#{n}");
        gen.getProbabilityOfOne().setValue(0.5);
        Collection<ProblemAndMetadataPair> result = gen.generateProblems();
        assertEquals(result.size(), 6);
        Iterator<ProblemAndMetadataPair> iterator = result.iterator();
        int n = 10;
        int m = 2 * n;
        double p = 0.5;
        while (iterator.hasNext()) {
            ProblemAndMetadataPair item = iterator.next();
            
            assertEquals(((SetPackingProblem)item.getProblem()).getVariablesCountN(), n);
            assertEquals(((SetPackingProblem)item.getProblem()).getConstraintsCountM(), m);
            assertEquals(((SetPackingProblemMetadata)item.getMetadata()).getProbabilityOfOne(), p, 0.000001);

            n++;
            m = 2 * n;
        }
    }
}