/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dborisenko.math.optimization;

import com.dborisenko.math.optimization.linear.LexDualSimplexSolver;
import com.dborisenko.math.optimization.linear.integer.DynamicProgrammingSolver;
import com.dborisenko.math.optimization.linear.integer.GomoryFirstSolver;
import com.dborisenko.math.optimization.linear.integer.LClassesExhaustionSolver;
import com.dborisenko.math.optimization.linear.lexDualSimplexClasses.ZeroColumnsAction;
import com.dborisenko.math.optimization.problems.SetPackingProblem;
import com.dborisenko.math.optimization.problems.generators.ProblemAndMetadataPair;
import com.dborisenko.math.optimization.problems.generators.UnweightedSetPackingProblemGenerator;
import com.dborisenko.math.optimization.problems.metadata.SetPackingProblemMetadata;
import com.dborisenko.math.optimization.problems.parsers.UnweightedSetPackingProblemParser;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import org.apache.commons.math.optimization.GoalType;
import org.apache.commons.math.optimization.OptimizationException;
import org.apache.commons.math.optimization.RealPointValuePair;
import org.apache.commons.math.optimization.linear.LinearConstraint;
import org.apache.commons.math.optimization.linear.LinearObjectiveFunction;
import org.apache.commons.math.optimization.linear.SimplexSolver;
import org.apache.commons.math.optimization.linear.UnboundedSolutionException;
import org.apache.commons.math.util.MathUtils;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Denis
 */
public class OptimizerTest {

    private double epsilon = 1.0e-6;
    private boolean testSimplex = false;
    private boolean testLDSimplex = false;
    private boolean testBigSimplex = false;
    private boolean testDynamic = true;
    private boolean testGomory = false;
    private boolean testLClasses = true;
    private int gomoryMaxIterations = 100;
    private boolean lightAssert = false;
    private boolean printSolutionVector = true;

    public OptimizerTest() {
    }

    @Test
    public void testA1() throws Exception {
        System.out.println("testA1");
        Dictionary<String, LoadedDynamicSolverResult> dynResults = getResults("test-samples/samples0/resultsFromDynamic.txt");

        solveAndAssertFromFile("test-samples/samples0/0_1.txt", dynResults);
    }

    @Test
    public void testA2() throws Exception {
        System.out.println("testA2");
        Dictionary<String, LoadedDynamicSolverResult> dynResults = getResults("test-samples/samples0/resultsFromDynamic.txt");

        solveAndAssertFromFile("test-samples/samples0/6.txt", dynResults);
    }

    @Test
    public void testA3() throws Exception {
        System.out.println("testA3");
        Dictionary<String, LoadedDynamicSolverResult> dynResults = getResults("test-samples/samples0/resultsFromDynamic.txt");

        solveAndAssertFromFile("test-samples/samples0/0_2.txt", dynResults);
    }

    @Test
    public void testA4() throws Exception {
        System.out.println("testA4");
        Dictionary<String, LoadedDynamicSolverResult> dynResults = getResults("test-samples/samples0/resultsFromDynamic.txt");

        solveAndAssertFromFile("test-samples/samples0/1.txt", dynResults);
    }

    @Test
    public void testA() throws Exception {
        System.out.println("testA");
        Dictionary<String, LoadedDynamicSolverResult> dynResults = getResults("test-samples/samples1/resultsFromDynamic.txt");

        solveAndAssertFromFile("test-samples/samples1/0.txt", dynResults);
    }

    @Test
    public void testSmallRandomGeneratedProblem() throws Exception {
        System.out.println("testSmallRandomGeneratedProblem");
        
        Random rnd = new Random();
        UnweightedSetPackingProblemGenerator gen = new UnweightedSetPackingProblemGenerator();
        gen.setProblemsCount(1);
        gen.getConstraintsCountM().setValue(rnd.nextInt(9) + 1);
        gen.getVariablesCountN().setValue(rnd.nextInt(9) + 1);
        gen.getProbabilityOfOne().setValue(rnd.nextDouble());

        SetPackingProblem problem = (SetPackingProblem)gen.generateProblems().get(0).getProblem();
        printMetadata(problem.getVariablesCountN(),
                problem.getConstraintsCountM(),
                gen.getProbabilityOfOne().getValue());
        solveAndAssert(problem.getObjectiveFunction(),
                problem.getLinearConstraints(),
                problem.getGoalType(), null);
    }

    @Test
    public void test10RandomGeneratedProblems() throws Exception {
        System.out.println("test10RandomGeneratedProblems");
        for (int i = 0; i < 10; i++) {
            Random rnd = new Random();
            UnweightedSetPackingProblemGenerator gen = new UnweightedSetPackingProblemGenerator();
            gen.setProblemsCount(1);
            gen.getConstraintsCountM().setValue(rnd.nextInt(99) + 1);
            gen.getVariablesCountN().setValue(rnd.nextInt(99) + 1);
            gen.getProbabilityOfOne().setValue(rnd.nextDouble());

            SetPackingProblem problem = (SetPackingProblem)gen.generateProblems().get(0).getProblem();
            printMetadata(problem.getVariablesCountN(),
                    problem.getConstraintsCountM(),
                    gen.getProbabilityOfOne().getValue());
            solveAndAssert(problem.getObjectiveFunction(),
                    problem.getLinearConstraints(),
                    problem.getGoalType(), null);
            int k = 0;
        }
    }

    @Test
    public void testGeneratedProblems() throws Exception {
        System.out.println("testGeneratedProblems");
        UnweightedSetPackingProblemGenerator gen = new UnweightedSetPackingProblemGenerator();
        gen.setProblemsCount(2);
        gen.getConstraintsCountM().setRange(25, 25, 100);
        gen.getVariablesCountN().setRange(25, 25, 100);
        gen.getProbabilityOfOne().setRange(0.01, 0.1, 1.0);

        List<ProblemAndMetadataPair> problems = gen.generateProblems();
        Iterator<ProblemAndMetadataPair> iterator = problems.iterator();
        while (iterator.hasNext()) {
            ProblemAndMetadataPair pair = iterator.next();
            SetPackingProblem problem = (SetPackingProblem)pair.getProblem();
            SetPackingProblemMetadata metadata = (SetPackingProblemMetadata)pair.getMetadata();
            printMetadata(problem.getVariablesCountN(), 
                    problem.getConstraintsCountM(),
                    metadata.getProbabilityOfOne());
            solveAndAssert(problem.getObjectiveFunction(),
                problem.getLinearConstraints(),
                problem.getGoalType(), null);
        }
    }

    @Test
    public void testFromFilesSamples0() throws Exception {
        System.out.println("testFromFilesSamples0");
        Dictionary<String, LoadedDynamicSolverResult> dynResults = getResults("test-samples/samples0/resultsFromDynamic.txt");

        for (int i = 0; i <= 295; i++) {
            try {
                solveAndAssertFromFile("test-samples/samples0/" + i + ".txt", dynResults);
            } catch (IOException e) {
            }
        }
    }

    @Test
    public void testFromFilesSamples1() throws Exception {
        System.out.println("testFromFilesSamples1");
        Dictionary<String, LoadedDynamicSolverResult> dynResults = getResults("test-samples/samples1/resultsFromDynamic.txt");

        for (int i = 0; i <= 830; i++) {
            try {
                solveAndAssertFromFile("test-samples/samples1/" + i + ".txt", dynResults);
            } catch (IOException e) {
            }
        }
    }

    private Dictionary<String, LoadedDynamicSolverResult> getResults(String fileName)
            throws Exception {
        Dictionary<String, LoadedDynamicSolverResult> dic = new Hashtable<String, LoadedDynamicSolverResult>();
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        String s;
        while((s = reader.readLine()) != null) {
            String[] split = s.split(" ");
            String fn = split[1];
            dic.put(fn, new LoadedDynamicSolverResult(fn,
                    Integer.parseInt(split[4]),
                    Integer.parseInt(split[7]),
                    Double.parseDouble(split[10]),
                    Integer.parseInt(split[13]),
                    Integer.parseInt(split[19])));
        }
        return dic;
    }

    private void solveAndAssertFromFile(String fileName, Dictionary<String, LoadedDynamicSolverResult> dynResults)
            throws ParseException, IOException, OptimizationException {
        System.out.println("Working with file: " + fileName);
        UnweightedSetPackingProblemParser parser = new UnweightedSetPackingProblemParser();
        parser.readFromFile(fileName);
        SetPackingProblem problem = parser.getProblem();

        solveAndAssert(problem.getObjectiveFunction(),
                problem.getLinearConstraints(),
                problem.getGoalType(),
                dynResults.get(fileName));
    }

    private SolverResult solveLDSMAndAssert(LinearObjectiveFunction f, List<LinearConstraint> constraints,
            GoalType goalType, OptimizerPrecision precision, ZeroColumnsAction zeroColumnsAction,
            SolverResult expected) {
        LexDualSimplexSolver solver = new LexDualSimplexSolver();
        RealPointValuePair solution = null;
        try {
            solver.setEpsilon(epsilon);
            solver.setGoalType(goalType);
            solver.setLinearConstraints(constraints);
            solver.setMaxIterations(Integer.MAX_VALUE);
            solver.setObjectiveFunction(f);
            solver.setZeroColumnsAction(zeroColumnsAction);
            solver.setPrecision(precision);

            solver.optimize();
            solution = solver.getOptimalPoint();
        } catch (OptimizationException e) {
        }
        String name = "LDS [" +
                (precision == OptimizerPrecision.DOUBLE ? "dbl" : "big") +
                "] " + (zeroColumnsAction == ZeroColumnsAction.REMOVE_COLUMNS ? "rem0" : "addC");
        printSolution(name, solution,
                solver.getTime(),
                solver.getIterations(), solver.getErrorMessage());

        if (expected != null && !expected.unbounded && solver.getStatus() != OptimizerStatus.UNBOUNDED_SOLUTION) {
            lightAssertTrue(expected.unresolvable == (solver.getStatus() == OptimizerStatus.ERROR));
            if (expected.solution != null && solution != null) {
                lightAssertEquals(expected.solution.getValue(), solution.getValue(), epsilon);
            } else if (expected.solution == null && solution == null) {
                lightAssertTrue(expected.solution == null && solution == null);
            } else {
                lightFail("simplexSolution and remZeroLDSSolution are not equal");
            }
        }
        return (solver.getStatus() != OptimizerStatus.UNBOUNDED_SOLUTION) ?
            new SolverResult(solver.getStatus() == OptimizerStatus.UNBOUNDED_SOLUTION,
                solver.getStatus() == OptimizerStatus.ERROR, solution) :
            expected;
    }

    private void solveAndAssert(LinearObjectiveFunction f, List<LinearConstraint> constraints,
            GoalType goalType, LoadedDynamicSolverResult expectedLoadedDynamic) {
        
        System.out.println("n: " + f.getCoefficients().getDimension() + " | m: " +
                constraints.size() + (expectedLoadedDynamic != null ? " | expected value: " +
                expectedLoadedDynamic.resultValue + " | expected countT: " + 
                expectedLoadedDynamic.countOfSetT : ""));
        //-----------------------
        boolean simplexUnresolvable = false;
        boolean simplexUnbounded = false;
        RealPointValuePair simplexSolution = null;
        long simplexTime = 0;
        int simplexIterations = 0;
        String simplexErrorMessage = null;
        SimplexSolver simplexSolver = new SimplexSolver(epsilon);

        if (testSimplex) {
            try {
                Date startDate = new Date();
                simplexSolver.setMaxIterations(Integer.MAX_VALUE);
                simplexSolution = simplexSolver.optimize(f, constraints, goalType, true);
                simplexTime = (new Date()).getTime() - startDate.getTime();
                simplexIterations = simplexSolver.getIterations();
            } catch (UnboundedSolutionException e) {
                simplexUnbounded = true;
                simplexErrorMessage = e.getMessage();
            } catch (OptimizationException e) {
                simplexUnresolvable = true;
                simplexErrorMessage = e.getMessage();
            }
            printSolution("Simplex", simplexSolution, simplexTime,
                    simplexIterations, simplexErrorMessage);
        }

        SolverResult expectedSimplex = new SolverResult(simplexUnbounded, simplexUnresolvable, simplexSolution);

        //-----------------------

        if (testLDSimplex) {
            expectedSimplex = solveLDSMAndAssert(f, constraints, goalType,
                    OptimizerPrecision.DOUBLE, ZeroColumnsAction.ADD_CONSTRAINTS,
                    expectedSimplex);

            expectedSimplex = solveLDSMAndAssert(f, constraints, goalType,
                    OptimizerPrecision.DOUBLE, ZeroColumnsAction.REMOVE_COLUMNS,
                    expectedSimplex);
        }

        if (testBigSimplex) {
            expectedSimplex = solveLDSMAndAssert(f, constraints, goalType,
                    OptimizerPrecision.BIG, ZeroColumnsAction.ADD_CONSTRAINTS,
                    expectedSimplex);

            expectedSimplex = solveLDSMAndAssert(f, constraints, goalType,
                    OptimizerPrecision.BIG, ZeroColumnsAction.REMOVE_COLUMNS,
                    expectedSimplex);
        }
        
        //-----------------------

        DynamicProgrammingSolver dynProgSolver = new DynamicProgrammingSolver();
        dynProgSolver.setEpsilon(epsilon);
        dynProgSolver.setObjectiveFunction(f);
        dynProgSolver.setLinearConstraints(constraints);
        dynProgSolver.setGoalType(goalType);
        dynProgSolver.setMaxIterations(Integer.MAX_VALUE);

        RealPointValuePair dynProgSolution = null;
        if (testDynamic) {
            try {
                dynProgSolver.optimize();
                dynProgSolution = dynProgSolver.getOptimalPoint();
            } catch (Exception e) {
            }
            printSolution("Dynamic prog", dynProgSolution,
                    dynProgSolver.getTime(),
                    dynProgSolver.getIterations(), 
                    dynProgSolver.getErrorMessage(),
                    "setOfVectorsTCount", dynProgSolver.getSetOfVectorsTCount());

            if (expectedLoadedDynamic != null) {
                if (dynProgSolution != null) {
                    lightAssertEquals(dynProgSolution.getValue(), expectedLoadedDynamic.resultValue, epsilon);
                } else {
                    lightFail("dynProgSolution and expected are not equal");
                }
                lightAssertTrue(dynProgSolver.getSetOfVectorsTCount() == expectedLoadedDynamic.countOfSetT);
            }
        }

        //-----------------------
        GomoryFirstSolver gomory1Solver = new GomoryFirstSolver();
        gomory1Solver.setEpsilon(epsilon);
        gomory1Solver.setObjectiveFunction(f);
        gomory1Solver.setLinearConstraints(constraints);
        gomory1Solver.setGoalType(GoalType.MAXIMIZE);
        gomory1Solver.setMaxIterations(gomoryMaxIterations);
        gomory1Solver.setMaxSimplexSolverIterations(Integer.MAX_VALUE);

        RealPointValuePair gomory1Solution = null;
        if (testGomory) {
            try {
                gomory1Solver.optimize();
                gomory1Solution = gomory1Solver.getOptimalPoint();
            } catch (Exception e) {
                System.out.println(e);
            }
            printSolution("Gomory 1", gomory1Solution,
                    gomory1Solver.getTime(),
                    gomory1Solver.getIterations(), 
                    gomory1Solver.getErrorMessage(),
                    "gomoryCuttingCount", gomory1Solver.getGomoryCuttingCount());

            if (dynProgSolution != null) {
                lightAssertTrue(gomory1Solver.getStatus() == dynProgSolver.getStatus());
                if (expectedSimplex != null && expectedSimplex.solution != null) {
                    lightAssertEquals(expectedSimplex.solution.getValue(), gomory1Solver.getSimplexValue(), epsilon);
                }
                if (gomory1Solution != null && dynProgSolution != null) {
                    lightAssertEquals(gomory1Solution.getValue(), dynProgSolution.getValue(), epsilon);
                } else if (gomory1Solution == null && dynProgSolution == null) {
                    lightAssertTrue(gomory1Solution == null && dynProgSolution == null);
                } else {
                    lightFail("gomory1Solution and dynProgSolution are not equal");
                }
            }
            if (expectedLoadedDynamic != null) {
                if (gomory1Solution != null) {
                    lightAssertEquals(gomory1Solution.getValue(), expectedLoadedDynamic.resultValue, epsilon);
                }
            }
        }

        //-----------------------
        LClassesExhaustionSolver lClassesSolver = new LClassesExhaustionSolver();
        lClassesSolver.setEpsilon(epsilon);
        lClassesSolver.setObjectiveFunction(f);
        lClassesSolver.setLinearConstraints(constraints);
        lClassesSolver.setGoalType(GoalType.MAXIMIZE);
        lClassesSolver.setMaxIterations(gomoryMaxIterations);
        lClassesSolver.setMaxSimplexSolverIterations(Integer.MAX_VALUE);

        RealPointValuePair lClassesSolution = null;
        if (testLClasses) {
            try {
                lClassesSolver.optimize();
                lClassesSolution = lClassesSolver.getOptimalPoint();
            } catch (Exception e) {
                System.out.println(e);
            }
            printSolution("L-Classes", lClassesSolution,
                    lClassesSolver.getTime(),
                    lClassesSolver.getIterations(),
                    lClassesSolver.getErrorMessage(),
                    "resolvableSolutions", lClassesSolver.getResolvableSolutions(),
                    "unresolvableSolutions", lClassesSolver.getUnresolvableSolutions());

            if (dynProgSolution != null) {
                lightAssertTrue(lClassesSolver.getStatus() == dynProgSolver.getStatus());
                if (lClassesSolution != null && dynProgSolution != null) {
                    lightAssertEquals(lClassesSolution.getValue(), dynProgSolution.getValue(), epsilon);
                } else if (lClassesSolution == null && dynProgSolution == null) {
                    lightAssertTrue(lClassesSolution == null && dynProgSolution == null);
                } else {
                    lightFail("lClassesSolution and dynProgSolution are not equal");
                }
            }
            if (gomory1Solution != null) {
                if (lClassesSolution != null && gomory1Solution != null) {
                    lightAssertEquals(lClassesSolution.getValue(), gomory1Solution.getValue(), epsilon);
                } else if (lClassesSolution == null && gomory1Solution == null) {
                    lightAssertTrue(lClassesSolution == null && gomory1Solution == null);
                } else {
                    lightFail("lClassesSolution and gomory1Solution are not equal");
                }
            }
            if (expectedLoadedDynamic != null) {
                if (lClassesSolution != null) {
                    lightAssertEquals(lClassesSolution.getValue(), expectedLoadedDynamic.resultValue, epsilon);
                }
            }
        }
    }

    protected void printSolution(String name,
            RealPointValuePair solution,
            long time,
            int iterations,
            String errorMessage, Object... args) {
        String format = "| %1$-15s | val: %2$-20s | time: %3$-10s | iter: %4$-10s | error: %5$-20s";
        String str = String.format(format,
                name,
                (solution != null) ? solution.getValue() : "null",
                time,
                iterations,
                errorMessage);
        try {
            for (int i = 0; i < args.length; i += 2) {
                str += " | " + args[i].toString() + ": " + args[i + 1].toString();
            }
        } catch (Exception ex) {
        }
        System.out.println(str);
        if (printSolutionVector) {
            for (int i = 0; i < solution.getPointRef().length; i++) {
                System.out.print(solution.getPointRef()[i] + " ");
            }
            System.out.println();
        }
    }

    protected void printMetadata(int n, int m, double p) {
        String format = "n:%1$-5s m:%2$-5s p:%3$-5s\n";
        System.out.format(format, n, m, p);
    }

    protected void lightAssertTrue(boolean condition) {
        if (!lightAssert) {
            assertTrue(condition);
            return;
        }
        if (!condition) {
            lightFail("Expected <true> was <false>");
        }
    }

    protected void lightAssertEquals(double expected, double actual, double delta) {
        if (!lightAssert) {
            assertEquals(expected, actual, delta);
            return;
        }
        if (!MathUtils.equals(expected, actual, delta)) {
            lightFail("Expected <" + expected + "> was <" + actual + ">.");
        }
    }

    protected void lightFail(String message) {
        if (!lightAssert) {
            fail(message);
            return;
        }
        System.out.println("\t===Assert lightFailed. " + message + ".===");
        System.out.println("\t===Call stack===");
        showCallStack();
        System.out.println("\t================");
    }

    public static void showCallStack()
    {
            StackTraceElement[] stackTraceElements =
                    Thread.currentThread().getStackTrace();
            for (int i=2 ; i<stackTraceElements.length; i++)
            {
                    StackTraceElement ste = stackTraceElements[i];
                String classname = ste.getClassName();
                String methodName = ste.getMethodName();
                int lineNumber = ste.getLineNumber();
                System.out.println("\t\t" + 
                    classname+"."+methodName+":"+lineNumber);
            }
    }

    class LoadedDynamicSolverResult {
        protected final String fileName;
        protected final int n;
        protected final int m;
        protected final Double resultValue;
        protected final int countOfSetT;
        protected final int countOfOnes;

        public LoadedDynamicSolverResult(String fileName, int n, int m,
                Double resultValue, int countOfSetT, int countOfOnes) {
            this.fileName = fileName;
            this.countOfOnes = countOfOnes;
            this.countOfSetT = countOfSetT;
            this.m = m;
            this.n = n;
            this.resultValue = resultValue;
        }
    }

    class SolverResult {
        protected final boolean unbounded;
        protected final boolean unresolvable;
        protected final RealPointValuePair solution;

        public SolverResult(boolean  unbounded, boolean  unresolvable,
                RealPointValuePair solution) {
            this.unbounded = unbounded;
            this.unresolvable = unresolvable;
            this.solution = solution;
        }
    }

}