package com.dborisenko.math.optimization.problems.session;

import com.dborisenko.math.optimization.problems.resolvers.OptimizationProblemResolver;
import java.util.Collection;
import java.util.Iterator;
import org.apache.commons.math.optimization.OptimizationException;

/**
 *
 * @author Denis Borisenko
 */
public class ResolverIterator implements Iterator<OptimizationProblemResolver> {

    private Iterator<OptimizationProblemResolver> iterator;

    public ResolverIterator(Collection<OptimizationProblemResolver> resolvers) {
        iterator = resolvers.iterator();
    }

    public OptimizationProblemResolver resolveNext() 
            throws OptimizationException {
        OptimizationProblemResolver resolver = iterator.next();
        resolver.resolve();
        return resolver;
    }

    public boolean hasNext() {
        return iterator.hasNext();
    }

    public OptimizationProblemResolver next() {
        return iterator.next();
    }

    public void remove() {
        iterator.remove();
    }
}
