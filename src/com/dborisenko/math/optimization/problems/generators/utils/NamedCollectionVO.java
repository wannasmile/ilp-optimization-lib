/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dborisenko.math.optimization.problems.generators.utils;

import java.util.Collection;

/**
 *
 * @author Denis
 */
public class NamedCollectionVO<ValueType> {
    protected final String name;
    protected final Collection<ValueType> collection;

    public NamedCollectionVO(String name, Collection<ValueType> collection) {
        this.name = name;
        this.collection = collection;
    }

    @Override
    public String toString() {
        return name;
    }
}
