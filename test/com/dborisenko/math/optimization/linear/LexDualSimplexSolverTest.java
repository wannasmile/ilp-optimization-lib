/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dborisenko.math.optimization.linear;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.math.optimization.GoalType;
import org.apache.commons.math.optimization.OptimizationException;
import org.apache.commons.math.optimization.RealPointValuePair;
import org.apache.commons.math.optimization.linear.LinearConstraint;
import org.apache.commons.math.optimization.linear.LinearObjectiveFunction;
import org.apache.commons.math.optimization.linear.Relationship;
import org.apache.commons.math.optimization.linear.SimplexSolver;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Denis
 */
public class LexDualSimplexSolverTest {

    private static final double epsilon = 1.0e-6;

    public LexDualSimplexSolverTest() {
    }

    @Test
    public void test00() throws Exception {
        System.out.println("test00");
        LinearObjectiveFunction f = new LinearObjectiveFunction(new double[] {1, 3, 3}, 0);
        ArrayList<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
        constraints.add(new LinearConstraint(new double[] {1, 2, -1}, Relationship.LEQ, 8));
        constraints.add(new LinearConstraint(new double[] {1, -1, 2}, Relationship.LEQ, 8));

        solveAndAssert(f, constraints, GoalType.MAXIMIZE, 16.0);
    }

    @Test
    public void test01() throws Exception {
        System.out.println("test01");
        LinearObjectiveFunction f = new LinearObjectiveFunction(new double[] {-3, 2}, 0);
        ArrayList<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
        constraints.add(new LinearConstraint(new double[] {-1, 1}, Relationship.GEQ, 1));
        constraints.add(new LinearConstraint(new double[] {2, -3}, Relationship.GEQ, 6));

        solveAndAssert(f, constraints, GoalType.MAXIMIZE, 10.0);
    }

    @Test
    public void test02() throws Exception {
        System.out.println("test02");
        LinearObjectiveFunction f = new LinearObjectiveFunction(new double[] {-1, -2}, 0);
        ArrayList<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
        constraints.add(new LinearConstraint(new double[] {1, 1}, Relationship.GEQ, 1));
        constraints.add(new LinearConstraint(new double[] {4, 1}, Relationship.LEQ, 2));
        constraints.add(new LinearConstraint(new double[] {1, 0}, Relationship.GEQ, 0));
        constraints.add(new LinearConstraint(new double[] {0, 1}, Relationship.GEQ, 0));

        solveAndAssert(f, constraints, GoalType.MAXIMIZE, null);
    }

    private void solveAndAssert(LinearObjectiveFunction f, List<LinearConstraint> constraints,
            GoalType goalType, Double maxArtificialValue) {
        boolean unresolvable1 = false;
        boolean unresolvable2 = false;
        RealPointValuePair solution1 = null;
        RealPointValuePair solution2 = null;

       try {
            Date start1 = new Date();
            SimplexSolver solver1 = new SimplexSolver(epsilon);
            solver1.setMaxIterations(Integer.MAX_VALUE);
            solution1 = solver1.optimize(f, constraints, goalType, true);
            printSolution("Simplex", solution1, solver1.getIterations(), start1);
        } catch (OptimizationException e) {
            unresolvable1 = true;
            System.out.println("SimplexSolver [" + e.toString() + "]         " + e.getMessage());
        }

        try {
            Date start2 = new Date();
            LexDualSimplexSolver solver2 = new LexDualSimplexSolver();
            solver2.maxArtificialValue = maxArtificialValue;
            solver2.setEpsilon(epsilon);
            solver2.setObjectiveFunction(f);
            solver2.setLinearConstraints(constraints);
            solver2.setGoalType(goalType);
            solver2.setMaxIterations(Integer.MAX_VALUE);
            solver2.optimize();
            solution2 = solver2.getOptimalPoint();
            printSolution("LexDual", solution2, solver2.getIterations(), start2);
        } catch (OptimizationException e) {
            unresolvable2 = true;
            System.out.println("LexDualSimplexSolver [" + e.toString() + "]  " + e.getMessage());
        }

        if (solution1 != null && solution2 != null) {
            assertEquals(solution1.getValue(), solution2.getValue(), epsilon);
        } else if (solution1 != null && solution2 == null) {
            fail("Solution from LexDualSimplexSolver == null, but SimplexSolver != null");
        } else if (solution1 == null && solution2 != null) {
            fail("Solution from SimplexSolver == null, but LexDualSimplexSolver != null");
        }

        assertEquals(unresolvable1,unresolvable2);
    }

    protected void printSolution(String name,
            RealPointValuePair solution,
            int iterations,
            Date start) {
        String format = "| %1$-10s | %2$-20s | time: %3$-10s | iter: %4$-10s\n";
        long time = (new Date()).getTime() - start.getTime();
        System.out.format(format, name, solution.getValue(), time, iterations);
    }

}