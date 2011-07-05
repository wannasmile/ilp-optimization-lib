package com.dborisenko.data;

import com.dborisenko.data.dao.ProblemSessionDAO;
import com.dborisenko.data.dao.impl.ProblemSessionDAOImpl;

/**
 *
 * @author Denis Borisenko
 */
public class MathDAO {
    private static MathDAO instance = null;

    public static MathDAO getInstance() {
        if (instance == null) {
            instance = new MathDAO();
        }
        return instance;
    }

    private ProblemSessionDAO sessionDAO = new ProblemSessionDAOImpl();

    private MathDAO() {
    }

    public ProblemSessionDAO getSessionDAO() {
        return sessionDAO;
    }
}
