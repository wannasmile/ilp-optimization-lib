/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dborisenko.math.vo;

/**
 *
 * @author Denis
 */
public class RangeVO<Type> {

    private Type startValue = null;
    private Type stopValue = null;
    private Type step = null;

    public RangeVO() {
    }

    public RangeVO(Type startValue, Type step, Type stopValue) {
        this.startValue = startValue;
        this.step = step;
        this.stopValue = stopValue;
    }

    public void setStartValue(Type value) {
        startValue = value;
    }
    public Type getStartValue() {
        return startValue;
    }

    public void setStopValue(Type value) {
        stopValue = value;
    }
    public Type getStopValue() {
        return stopValue;
    }

    public void setStep(Type value) {
        step = value;
    }
    public Type getStep() {
        return step;
    }

    @Override
    public String toString() {
        return "от [" + getStartValue().toString() + "] : шаг [" + getStep().toString() +
                "] : до [" + getStopValue().toString() + "]";
    }
}
