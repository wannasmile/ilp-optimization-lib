/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dborisenko.math.optimization.problems.parsers;

import com.dborisenko.math.optimization.problems.SetPackingProblem;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Iterator;
import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.math.linear.RealVector;
import org.apache.commons.math.optimization.linear.LinearConstraint;
import org.apache.commons.math.util.MathUtils;

/**
 *
 * @author Denis
 */
public class UnweightedSetPackingProblemDeparser {

    private double epsilon = 10e-6;

    protected final SetPackingProblem problem;

    public UnweightedSetPackingProblemDeparser(SetPackingProblem problem) {
        this.problem = problem;
    }

    public String deparse() {
        return deparse(problem);
    }

    protected String deparse(SetPackingProblem problem) {
        Collection<LinearConstraint> constraints = problem.getLinearConstraints();
        String result = "";
        result += "p set " +
                problem.getObjectiveFunction().getCoefficients().getDimension() + " " +
                constraints.size() + "\n";
        Iterator<LinearConstraint> iterator = constraints.iterator();
        while (iterator.hasNext()) {
            result += "s ";
            RealVector coef = iterator.next().getCoefficients();
            for (int j = 0; j < coef.getDimension(); j++) {
                if (MathUtils.equals(coef.getEntry(j), 1.0, epsilon)) {
                    result += (j + 1) + " ";
                }
            }
            result += "\n";
        }
        return result;
    }

    public void write(BufferedWriter writer)
            throws IOException {
        writer.write(deparse());
        writer.flush();
    }

    public void write(Writer file)
            throws IOException {
        write(new BufferedWriter(file));
    }

    public void writeToFile(String fileName)
            throws IOException {
        write(new FileWriter(fileName));
    }

}
