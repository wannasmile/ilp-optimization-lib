package com.dborisenko.math.optimization.linear.integer;

import java.util.ArrayList;
import org.apache.commons.math.optimization.GoalType;
import org.apache.commons.math.optimization.RealPointValuePair;
import org.apache.commons.math.optimization.linear.LinearConstraint;
import org.apache.commons.math.optimization.linear.LinearObjectiveFunction;
import org.apache.commons.math.optimization.linear.Relationship;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Denis
 */
public class GomoryFirstSolverTest {

    private static final double epsilon = 1.0e-6;

    public GomoryFirstSolverTest() {
    }

    @Test
    public void test1()
            throws Exception {
        LinearObjectiveFunction f = new LinearObjectiveFunction(new double[] {-1, -2}, 0);
        ArrayList<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
        constraints.add(new LinearConstraint(new double[] {1, 1}, Relationship.GEQ, 1));
        constraints.add(new LinearConstraint(new double[] {4, 1}, Relationship.LEQ, 2));
        constraints.add(new LinearConstraint(new double[] {1, 0}, Relationship.GEQ, 0));
        constraints.add(new LinearConstraint(new double[] {0, 1}, Relationship.GEQ, 0));

        GomoryFirstSolver solver = new GomoryFirstSolver();
        solver.setEpsilon(epsilon);
        solver.setObjectiveFunction(f);
        solver.setLinearConstraints(constraints);
        solver.setGoalType(GoalType.MAXIMIZE);

        solver.setMaxIterations(Integer.MAX_VALUE);
        solver.setMaxArtificialValue(1.0);
        solver.optimize();
        RealPointValuePair solution = solver.getOptimalPoint();

        assertEquals(-2.0, solution.getValue(), epsilon);
        assertEquals(0.0, solution.getPointRef()[0], epsilon);
        assertEquals(1.0, solution.getPointRef()[1], epsilon);
    }

    @Test
    public void test2()
            throws Exception {
        LinearObjectiveFunction f = new LinearObjectiveFunction(new double[] {-1, 2}, 0);
        ArrayList<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
        constraints.add(new LinearConstraint(new double[] {3, 3}, Relationship.LEQ, 4));
        constraints.add(new LinearConstraint(new double[] {4, 1}, Relationship.LEQ, 2));
        constraints.add(new LinearConstraint(new double[] {1, 0}, Relationship.GEQ, 0));
        constraints.add(new LinearConstraint(new double[] {0, 1}, Relationship.GEQ, 0));

        GomoryFirstSolver solver = new GomoryFirstSolver();
        solver.setEpsilon(epsilon);
        solver.setObjectiveFunction(f);
        solver.setLinearConstraints(constraints);
        solver.setGoalType(GoalType.MAXIMIZE);

        solver.setMaxIterations(Integer.MAX_VALUE);
        solver.setMaxArtificialValue(100.0);//4.0 / 3.0);
        solver.optimize();
        RealPointValuePair solution = solver.getOptimalPoint();

        assertEquals(2.0, solution.getValue(), epsilon);
        assertEquals(0.0, solution.getPointRef()[0], epsilon);
        assertEquals(1.0, solution.getPointRef()[1], epsilon);
    }

    @Test
    public void test3()
            throws Exception {
        LinearObjectiveFunction f = new LinearObjectiveFunction(new double[] {1, 1}, 0);
        ArrayList<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
        constraints.add(new LinearConstraint(new double[] {2, 1}, Relationship.LEQ, 5));
        constraints.add(new LinearConstraint(new double[] {-2, 2}, Relationship.LEQ, 5));
        constraints.add(new LinearConstraint(new double[] {1, 0}, Relationship.GEQ, 0));
        constraints.add(new LinearConstraint(new double[] {0, 1}, Relationship.GEQ, 0));

        GomoryFirstSolver solver = new GomoryFirstSolver();
        solver.setEpsilon(epsilon);
        solver.setObjectiveFunction(f);
        solver.setLinearConstraints(constraints);
        solver.setGoalType(GoalType.MAXIMIZE);

        solver.setMaxIterations(Integer.MAX_VALUE);
        solver.setMaxArtificialValue(100.0);//7.0 / 6.0 + 8.0 / 3.0);
        solver.optimize();
        RealPointValuePair solution = solver.getOptimalPoint();

        assertEquals(3.0, solution.getValue(), epsilon);
        assertEquals(2.0, solution.getPointRef()[0], epsilon);
        assertEquals(1.0, solution.getPointRef()[1], epsilon);
    }

    @Test
    public void test4()
            throws Exception {
        LinearObjectiveFunction f = new LinearObjectiveFunction(new double[] {1, 1}, 0);
        ArrayList<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
        constraints.add(new LinearConstraint(new double[] {2, 5}, Relationship.LEQ, 16));
        constraints.add(new LinearConstraint(new double[] {6, 5}, Relationship.LEQ, 30));
        constraints.add(new LinearConstraint(new double[] {1, 0}, Relationship.GEQ, 0));
        constraints.add(new LinearConstraint(new double[] {0, 1}, Relationship.GEQ, 0));

        GomoryFirstSolver solver = new GomoryFirstSolver();
        solver.setEpsilon(epsilon);
        solver.setObjectiveFunction(f);
        solver.setLinearConstraints(constraints);
        solver.setGoalType(GoalType.MAXIMIZE);

        solver.setMaxIterations(Integer.MAX_VALUE);
        solver.setMaxArtificialValue(5.0);
        solver.optimize();
        RealPointValuePair solution = solver.getOptimalPoint();

        assertEquals(5.0, solution.getValue(), epsilon);
        assertEquals(5.0, solution.getPointRef()[0], epsilon);
        assertEquals(0.0, solution.getPointRef()[1], epsilon);
    }

    @Test
    public void test5()
            throws Exception {
        LinearObjectiveFunction f = new LinearObjectiveFunction(new double[] {2, 1}, 0);
        ArrayList<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
        constraints.add(new LinearConstraint(new double[] {2, 0}, Relationship.LEQ, 3));
        constraints.add(new LinearConstraint(new double[] {2, 3}, Relationship.LEQ, 6));
        constraints.add(new LinearConstraint(new double[] {1, 0}, Relationship.GEQ, 0));
        constraints.add(new LinearConstraint(new double[] {0, 1}, Relationship.GEQ, 0));

        GomoryFirstSolver solver = new GomoryFirstSolver();
        solver.setEpsilon(epsilon);
        solver.setObjectiveFunction(f);
        solver.setLinearConstraints(constraints);
        solver.setGoalType(GoalType.MAXIMIZE);

        solver.setMaxIterations(Integer.MAX_VALUE);
        solver.setMaxArtificialValue(100.0);
        solver.optimize();
        RealPointValuePair solution = solver.getOptimalPoint();

        assertEquals(3.0, solution.getValue(), epsilon);
        assertEquals(1.0, solution.getPointRef()[0], epsilon);
        assertEquals(1.0, solution.getPointRef()[1], epsilon);
    }

    @Test
    public void test6()
            throws Exception {
        LinearObjectiveFunction f = new LinearObjectiveFunction(new double[] {1, 2}, 0);
        ArrayList<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
        constraints.add(new LinearConstraint(new double[] {1, 1}, Relationship.LEQ, 5));
        constraints.add(new LinearConstraint(new double[] {1, 3}, Relationship.LEQ, 8));
        constraints.add(new LinearConstraint(new double[] {0, 4}, Relationship.LEQ, 7));
        constraints.add(new LinearConstraint(new double[] {1, 0}, Relationship.GEQ, 0));
        constraints.add(new LinearConstraint(new double[] {0, 1}, Relationship.GEQ, 0));

        GomoryFirstSolver solver = new GomoryFirstSolver();
        solver.setEpsilon(epsilon);
        solver.setObjectiveFunction(f);
        solver.setLinearConstraints(constraints);
        solver.setGoalType(GoalType.MAXIMIZE);

        solver.setMaxIterations(Integer.MAX_VALUE);
        solver.setMaxArtificialValue(5.0);
        solver.optimize();
        RealPointValuePair solution = solver.getOptimalPoint();

        assertEquals(6.0, solution.getValue(), epsilon);
        assertEquals(4.0, solution.getPointRef()[0], epsilon);
        assertEquals(1.0, solution.getPointRef()[1], epsilon);
    }
}