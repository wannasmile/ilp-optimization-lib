package com.dborisenko.math.optimization.problems.metadata;

import javax.persistence.Entity;

/**
 *
 * @author Denis
 */
@Entity
public class SetPackingProblemMetadata extends ProblemMetadata {

    private static final long serialVersionUID = 1L;

    private Double probabilityOfOne;
    private String evalProbabilityOfOne;
    private String evalVariablesCountN;
    private String evalConstraintsCountM;
    private String sourceFileName;

    public SetPackingProblemMetadata(Double probabilityOfOne) {
        this.probabilityOfOne = probabilityOfOne;
    }

    public SetPackingProblemMetadata() {
    }

    public Double getProbabilityOfOne() {
        return probabilityOfOne;
    }
    public void setProbabilityOfOne(Double probabilityOfOne) {
        this.probabilityOfOne = probabilityOfOne;
    }

    public String getEvalProbabilityOfOne() {
        return evalProbabilityOfOne;
    }
    public void setEvalProbabilityOfOne(String evalProbabilityOfOne) {
        this.evalProbabilityOfOne = evalProbabilityOfOne;
    }

    public String getEvalVariablesCountN() {
        return evalVariablesCountN;
    }
    public void setEvalVariablesCountN(String evalVariablesCountN) {
        this.evalVariablesCountN = evalVariablesCountN;
    }

    public String getEvalConstraintsCountM() {
        return evalConstraintsCountM;
    }
    public void setEvalConstraintsCountM(String evalConstraintsCountM) {
        this.evalConstraintsCountM = evalConstraintsCountM;
    }

    public String getSourceFileName() {
        return sourceFileName;
    }
    public void setSourceFileName(String sourceFileName) {
        this.sourceFileName = sourceFileName;
    }
}
