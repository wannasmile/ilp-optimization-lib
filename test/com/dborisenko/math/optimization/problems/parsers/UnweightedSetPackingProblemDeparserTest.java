/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dborisenko.math.optimization.problems.parsers;

import com.dborisenko.math.optimization.problems.SetPackingProblem;
import java.util.ArrayList;
import org.apache.commons.math.optimization.linear.LinearConstraint;
import org.apache.commons.math.optimization.linear.LinearObjectiveFunction;
import org.apache.commons.math.optimization.linear.Relationship;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Denis
 */
public class UnweightedSetPackingProblemDeparserTest {

    public UnweightedSetPackingProblemDeparserTest() {
    }

    @Test
    public void testDeparse() {
        LinearObjectiveFunction f = new LinearObjectiveFunction(new double[] {1, 1}, 0);
        ArrayList<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
        constraints.add(new LinearConstraint(new double[] {1, 1}, Relationship.LEQ, 1));
        constraints.add(new LinearConstraint(new double[] {0, 1}, Relationship.LEQ, 1));
        constraints.add(new LinearConstraint(new double[] {1, 0}, Relationship.LEQ, 1));
        constraints.add(new LinearConstraint(new double[] {0, 1}, Relationship.LEQ, 1));


        UnweightedSetPackingProblemDeparser deparser = 
                new UnweightedSetPackingProblemDeparser(new SetPackingProblem(f, constraints));
        String result = deparser.deparse();
        assertEquals(result, "p set 2 4\ns 1 2 \ns 2 \ns 1 \ns 2 \n");
    }

}