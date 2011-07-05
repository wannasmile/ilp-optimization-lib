/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dborisenko.math.linear.relations;

import org.apache.commons.math.linear.AbstractRealVector;
import org.apache.commons.math.linear.ArrayRealVector;
import org.apache.commons.math.linear.RealVector;
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
public class LexLinearOrderTest {

    public LexLinearOrderTest() {
    }

    /**
     * Test of compareWithZero method, of class LexLinearOrder.
     */
    @Test
    public void testCompareWithZero() {
        RealVector v = new ArrayRealVector(new double[] {0, 0, 0, 0});
        double eps = 0.000001;
        int expResult = 0;
        int result = LexLinearOrder.compareWithZero(v, eps);
        assertEquals(expResult, result);

        v = new ArrayRealVector(new double[] {0, 1, 0, 0});
        expResult = 1;
        result = LexLinearOrder.compareWithZero(v, eps);
        assertEquals(expResult, result);

        v = new ArrayRealVector(new double[] {0, 0, 0, -10});
        expResult = -1;
        result = LexLinearOrder.compareWithZero(v, eps);
        assertEquals(expResult, result);
    }

    /**
     * Test of compareVectors method, of class LexLinearOrder.
     */
    @Test
    public void testCompareVectors() {
        RealVector v1 = new ArrayRealVector(new double[] {0, 0, 1, 0});
        RealVector v2 = new ArrayRealVector(new double[] {0, 0, 1, 0});
        double eps = 0.000001;
        int expResult = 0;
        int result = LexLinearOrder.compareVectors(v1, v2, eps);
        assertEquals(expResult, result);

        v1 = new ArrayRealVector(new double[] {0, 1, 0, 0});
        v2 = new ArrayRealVector(new double[] {0, 0, 1, 0});
        expResult = 1;
        result = LexLinearOrder.compareVectors(v1, v2, eps);
        assertEquals(expResult, result);

        v1 = new ArrayRealVector(new double[] {0, -1, 0, 10});
        v2 = new ArrayRealVector(new double[] {0, 1, 1, -10});
        expResult = -1;
        result = LexLinearOrder.compareVectors(v1, v2, eps);
        assertEquals(expResult, result);
    }

}