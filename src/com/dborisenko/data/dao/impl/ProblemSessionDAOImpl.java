/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dborisenko.data.dao.impl;

import com.dborisenko.data.MathHibernateUtil;
import com.dborisenko.data.dao.ProblemSessionDAO;
import com.dborisenko.math.optimization.problems.session.OptimizationProblemsSession;
import com.dborisenko.math.optimization.problems.session.ProblemsSession;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.collection.PersistentCollection;
import org.hibernate.impl.SessionImpl;

/**
 *
 * @author Denis Borisenko
 */
public class ProblemSessionDAOImpl implements ProblemSessionDAO {

    public void addProblemSession(OptimizationProblemsSession problemSession) throws HibernateException {
        Session session = null;
        try {
            session = MathHibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.save(problemSession);
            session.getTransaction().commit();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    public void updateProblemSession(OptimizationProblemsSession problemSession) throws HibernateException {
        Session session = null;
        try {
            session = MathHibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.update(problemSession);
            session.getTransaction().commit();
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    public List<OptimizationProblemsSession> getAllProblemSessions() {
        Session session = null;
        List<OptimizationProblemsSession> result = new ArrayList<OptimizationProblemsSession>();
        try {
            session = MathHibernateUtil.getSessionFactory().openSession();
            result = session.createCriteria(ProblemsSession.class).list();
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        return result;
    }

    public void initializeProblemSessions(Collection<OptimizationProblemsSession> sessions) {
//        Session session = null;
//        List<OptimizationProblemsSession> result = new ArrayList<OptimizationProblemsSession>();
//        try {
//            session = MathHibernateUtil.getSessionFactory().openSession();
//
//            Iterator<OptimizationProblemsSession> sessionsIterator = sessions.iterator();
//            while (sessionsIterator.hasNext()) {
//                OptimizationProblemsSession currSession = sessionsIterator.next();
////                OptimizationProblemsSession loadedSession = session.loa
//            }
//        } finally {
//            if (session != null && session.isOpen()) {
////                session.close();
//            }
//        }
    }
}
