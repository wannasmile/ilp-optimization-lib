/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dborisenko.data.dao;

import com.dborisenko.math.optimization.problems.session.OptimizationProblemsSession;
import java.util.Collection;
import java.util.List;
import org.hibernate.HibernateException;

/**
 *
 * @author Denis Borisenko
 */
public interface ProblemSessionDAO {
    public void addProblemSession(OptimizationProblemsSession problemSession)
            throws HibernateException;
    public void updateProblemSession(OptimizationProblemsSession problemSession)
            throws HibernateException;
    public List<OptimizationProblemsSession> getAllProblemSessions();
    public void initializeProblemSessions(Collection<OptimizationProblemsSession> sessions);
}
