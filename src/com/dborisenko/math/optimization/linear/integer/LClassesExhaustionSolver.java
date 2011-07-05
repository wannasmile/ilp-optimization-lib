package com.dborisenko.math.optimization.linear.integer;

import com.dborisenko.math.optimization.linear.LexDualSimplexSolver;
import com.dborisenko.math.optimization.linear.integer.lClassesExhaustionClasses.LClassesExhaustionLexDualSimplexTableauFactory;
import com.dborisenko.math.optimization.linear.integer.lClassesExhaustionClasses.LClassesExhaustionSimplexTableau;
import com.dborisenko.math.optimization.linear.integer.lClassesExhaustionClasses.StepUpDownNextAction;
import com.dborisenko.math.utils.IntegerUtils;
import javax.persistence.Entity;
import org.apache.commons.math.optimization.OptimizationException;
import org.apache.commons.math.optimization.RealPointValuePair;
import org.apache.commons.math.optimization.linear.UnboundedSolutionException;
import org.apache.commons.math.util.MathUtils;

/**
 * Алгоритм перебора L-классов.
 * @author Denis Borisenko
 */
@Entity
public class LClassesExhaustionSolver extends IntegerLinearSolver {

    private static final long serialVersionUID = 1L;
    
    private transient Double delta = 1.0;
    protected transient Double recordValue;
    protected transient Integer currentColumnIndexP;
    protected transient Double minParticipatingVarsColumnIndexConstraint;
    
    protected transient LexDualSimplexSolver lastSimplexSolver;
    protected transient RealPointValuePair solutionX1;
    protected transient RealPointValuePair solutionX2;
    protected transient RealPointValuePair solution;
    protected transient RealPointValuePair solutionX;
    private Integer resolvableSolutions;
    private Integer unresolvableSolutions;
    private Integer integerSolutions;

    private transient int maxSimplexSolverIterations = Integer.MAX_VALUE;

    public LClassesExhaustionSolver() {
    }

    public int getMaxSimplexSolverIterations() {
        return maxSimplexSolverIterations;
    }
    public void setMaxSimplexSolverIterations(int maxSimplexSolverIterations) {
        this.maxSimplexSolverIterations = maxSimplexSolverIterations;
    }

    protected void initSimplexSolver() {
        initSimplexSolver(null, false, false, false);
    }

    protected void initSimplexSolver(boolean useDefaultFactory) {
        initSimplexSolver(null, true, useDefaultFactory, false);
    }

    protected void initSimplexSolver(Integer minParticipatingVarsColumnIndex,
            boolean useObjectiveFunction) {
        initSimplexSolver(minParticipatingVarsColumnIndex, useObjectiveFunction, false, false);
    }

    protected void initSimplexSolver(Integer minParticipatingVarsColumnIndex,
            boolean useObjectiveFunction, 
            boolean useDefaultFactory,
            boolean useDefaultArtificialBehaviour) {
        lastSimplexSolver = new LexDualSimplexSolver();
        if (!useDefaultFactory) {
            lastSimplexSolver.setTableauFactory(new LClassesExhaustionLexDualSimplexTableauFactory(
                    minParticipatingVarsColumnIndex, 
                    useObjectiveFunction,
                    useDefaultArtificialBehaviour
                    ));
        }
        lastSimplexSolver.setEpsilon(getEpsilon());
        lastSimplexSolver.setGoalType(getGoalType());
        lastSimplexSolver.setMaxIterations(getMaxSimplexSolverIterations());
        lastSimplexSolver.setLinearConstraints(getLinearConstraints());
        lastSimplexSolver.setObjectiveFunction(getObjectiveFunction());
        lastSimplexSolver.initTableau();

        if (!useDefaultFactory) {
            LClassesExhaustionSimplexTableau tableau = (LClassesExhaustionSimplexTableau)lastSimplexSolver.getTableau();

            if (minParticipatingVarsColumnIndex != null && minParticipatingVarsColumnIndexConstraint != null) {
                tableau.appendConstraintToVariableColumn(0,
                        minParticipatingVarsColumnIndexConstraint);
            }
            if (recordValue != null && delta != null) {
                tableau.appendRecordRow(recordValue, delta);
            }
        }
    }

    @Override
    protected void doOptimize()
            throws OptimizationException {

        resolvableSolutions = 0;
        unresolvableSolutions = 0;
        integerSolutions = 0;

        initSimplexSolver(true);
        lastSimplexSolver.optimize();
        RealPointValuePair optSolution = lastSimplexSolver.getOptimalPoint();

        if (IntegerUtils.isInteger(optSolution.getPointRef(), epsilon)) {
            integerSolutions++;
            setOptimalPoint(optSolution);
            return;
        }

//        recordValue = getMaxObjectiveCoefficient();

        initSimplexSolver(null, false, false, true);
        lastSimplexSolver.optimize();

        solutionX1 = lastSimplexSolver.getOptimalPoint();

//        printSolutionX1("Init");

//        recordValue = getMaxObjectiveCoefficient();

        StepUpDownNextAction nextAction = StepUpDownNextAction.INITIALIZATION;
        while (!isOptimal(nextAction)) {
            nextAction = doIteration(nextAction);
        }

        setOptimalPoint(solution);
    }

    protected Double getMaxObjectiveCoefficient() {
        double[] coeff = getObjectiveFunction().getCoefficients().getData();
        Double max = null;
        for (int i = 0; i < coeff.length; i++) {
            if (max == null || MathUtils.compareTo(coeff[i], max, getEpsilon()) > 0) {
                max = coeff[i];
            }
        }
        return max;
    }

    protected boolean isOptimal(StepUpDownNextAction action)
            throws UnboundedSolutionException {
        if (action == StepUpDownNextAction.UNRESOLVABLE) {
            throw new UnboundedSolutionException();
        }
        return action == StepUpDownNextAction.SOLVED;
    }

    protected StepUpDownNextAction doIteration(final StepUpDownNextAction action)
            throws OptimizationException {

        incrementIterationsCounter();

        switch (action) {
            case INITIALIZATION:
                return initSearch();
            case SEARCH_NEXT_L_CLASS_DOWN:
                return searchNextLClassDown();
            case SEARCH_NEXT_L_CLASS_UP:
                return searchNextLClassUp();
            case OPTIMUM_FOUND:
                return doOptimalValue();
        }
        return null;
    }

    protected void updateRecordAndSolutionX() {
        double newRecValue = getObjectiveFunction().getValue(solutionX1.getPointRef());
        if (recordValue == null || newRecValue >= recordValue) {
            solutionX = solutionX1;
        }
        recordValue = newRecValue;
    }

    protected StepUpDownNextAction initSearch()
            throws OptimizationException {
        if (IntegerUtils.isInteger(solutionX1.getPointRef(), epsilon)) {
            integerSolutions++;
            updateRecordAndSolutionX();
            solutionX2 = solutionX1;
            currentColumnIndexP = solutionX1.getPointRef().length;
            return searchNextLClassUp();
        } else {
            return searchNextLClassDown();
        }
    }

    protected RealPointValuePair convertToApplicableSolution(RealPointValuePair optSolution,
            Integer startIndex) {
        if (optSolution != null && startIndex != null) {
            double[] point = new double[getObjectiveFunction().getCoefficients().getDimension()];
            int offset = Math.abs(point.length - optSolution.getPointRef().length);
            for (int i = point.length - 1; i >= 0; i--) {
                if (i < startIndex) {
                    point[i] = solutionX2.getPointRef()[i];
                } else if (i - offset >= 0) {
                    point[i] = optSolution.getPointRef()[i - offset];
                }
            }
            return new RealPointValuePair(point, optSolution.getValue());
        }
        return optSolution;
    }

    protected void printSolutionX1(String label) {
        System.out.print(label + ": ");
        double sum = 0.0;
        if (solutionX1 != null) {
            for (int i = 0; i < solutionX1.getPointRef().length; i++) {
                System.out.print(solutionX1.getPointRef()[i] + " ");
                sum += solutionX1.getPointRef()[i];
            }
        }
        System.out.println("(" + sum + ")");
    }

    protected void incrementCounters(boolean noSolution) {
        if (noSolution) {
            unresolvableSolutions++;
        } else {
            resolvableSolutions++;
        }
    }

    protected StepUpDownNextAction searchNextLClassDown()
            throws OptimizationException {
        solutionX2 = solutionX1;
        currentColumnIndexP = getMinNonIntegerIndex(solutionX2.getPointRef());

        try {
            if (currentColumnIndexP != null) {
                minParticipatingVarsColumnIndexConstraint = IntegerUtils.getIntegerPart(solutionX2.getPointRef()[currentColumnIndexP]);
            }
            initSimplexSolver(currentColumnIndexP, false);
            lastSimplexSolver.optimize();
            solutionX1 = convertToApplicableSolution(lastSimplexSolver.getOptimalPoint(),
                    currentColumnIndexP);
        } catch (OptimizationException ex) {
            solutionX1 = null;
        }

        minParticipatingVarsColumnIndexConstraint = null;
//        printSolutionX1("Down");

        boolean noSolution = (solutionX1 == null);
        incrementCounters(noSolution);

        if (noSolution && currentColumnIndexP == 0) {
            return StepUpDownNextAction.OPTIMUM_FOUND;
        } else if (noSolution && currentColumnIndexP > 0) {
            return StepUpDownNextAction.SEARCH_NEXT_L_CLASS_UP;
        } else if (IntegerUtils.isInteger(solutionX1.getPointRef(), getEpsilon())) {
            integerSolutions++;
            updateRecordAndSolutionX();
//            recordValue = getObjectiveFunction().getValue(solutionX1.getPointRef());
            currentColumnIndexP = solutionX1.getPointRef().length;
            solutionX2 = solutionX1;
            return StepUpDownNextAction.SEARCH_NEXT_L_CLASS_UP;
        }
        return StepUpDownNextAction.SEARCH_NEXT_L_CLASS_DOWN;
    }

    protected StepUpDownNextAction searchNextLClassUp()
            throws OptimizationException {
//        if (solutionX2 == null) {
//            solutionX2 = solutionX1;
//            return StepUpDownNextAction.OPTIMUM_FOUND;
//        }
        Integer currentColumnIndexPhi = getMaxNonZeroIndex(solutionX2.getPointRef());

        if (currentColumnIndexPhi == null) {
            return StepUpDownNextAction.OPTIMUM_FOUND;
        }

        try {
            minParticipatingVarsColumnIndexConstraint = solutionX2.getPointRef()[currentColumnIndexPhi] - 1;
            initSimplexSolver(currentColumnIndexPhi, false);
            lastSimplexSolver.optimize();
            solutionX1 = convertToApplicableSolution(lastSimplexSolver.getOptimalPoint(),
                    currentColumnIndexPhi);
        } catch (OptimizationException ex) {
            solutionX1 = null;
        }

        minParticipatingVarsColumnIndexConstraint = null;
//        printSolutionX1("Up  ");

        boolean noSolution = (solutionX1 == null);
        incrementCounters(noSolution);

        if (noSolution && currentColumnIndexPhi == 0) {
            return StepUpDownNextAction.OPTIMUM_FOUND;
        } else if (noSolution && currentColumnIndexPhi > 0) {
            currentColumnIndexP = currentColumnIndexPhi;
            return StepUpDownNextAction.SEARCH_NEXT_L_CLASS_UP;
        } else if (IntegerUtils.isInteger(solutionX1.getPointRef(), getEpsilon())) {
            integerSolutions++;
            updateRecordAndSolutionX();
//            recordValue = getObjectiveFunction().getValue(solutionX1.getPointRef());
            currentColumnIndexP = solutionX1.getPointRef().length;
            solutionX2 = solutionX1;
            return StepUpDownNextAction.SEARCH_NEXT_L_CLASS_UP;
        }
//        System.out.println("!!!!!");
        return StepUpDownNextAction.SEARCH_NEXT_L_CLASS_DOWN;
    }

    protected StepUpDownNextAction doOptimalValue() {
        if (solutionX != null) {
             solution = new RealPointValuePair(solutionX.getPoint(), recordValue);
             return StepUpDownNextAction.SOLVED;
        } else {
            return StepUpDownNextAction.UNRESOLVABLE;
        }
    }

    protected Integer getMinNonIntegerIndex(double[] point) {
        for (int i = 0; i < point.length; i++) {
            if (!IntegerUtils.isInteger(point[i], getEpsilon())) {
                return i;
            }
        }
        return null;
    }

    protected Integer getMaxNonZeroIndex(double[] point) {
        if (currentColumnIndexP != null) {
            for (int i = currentColumnIndexP - 1; i >= 0; i--) {
                if (MathUtils.compareTo(point[i], 0.0, getEpsilon()) > 0) {
                    return i;
                }
            }
        }
        return null;
    }

    public Double getDelta() {
        return delta;
    }
    public void setDelta(Double delta) {
        this.delta = delta;
    }

    /**
     * @return the resolvableSolutions
     */
    public Integer getResolvableSolutions() {
        return resolvableSolutions;
    }

    /**
     * @param resolvableSolutions the resolvableSolutions to set
     */
    public void setResolvableSolutions(Integer resolvableSolutions) {
        this.resolvableSolutions = resolvableSolutions;
    }

    /**
     * @return the unresolvableSolutions
     */
    public Integer getUnresolvableSolutions() {
        return unresolvableSolutions;
    }

    /**
     * @param unresolvableSolutions the unresolvableSolutions to set
     */
    public void setUnresolvableSolutions(Integer unresolvableSolutions) {
        this.unresolvableSolutions = unresolvableSolutions;
    }

    /**
     * @return the integerSolutions
     */
    public Integer getIntegerSolutions() {
        return integerSolutions;
    }

    /**
     * @param integerSolutions the integerSolutions to set
     */
    public void setIntegerSolutions(Integer integerSolutions) {
        this.integerSolutions = integerSolutions;
    }
}