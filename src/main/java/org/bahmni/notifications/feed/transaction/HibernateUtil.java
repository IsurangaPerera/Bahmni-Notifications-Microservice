package org.bahmni.notifications.feed.transaction;

import org.apache.log4j.Logger;
import org.bahmni.notifications.feed.common.LogEvent;
import org.hibernate.*;
import org.hibernate.cfg.Configuration;
import org.hibernate.connection.C3P0ConnectionProvider;

public class HibernateUtil {

    private static Configuration configuration;
    private static SessionFactory sessionFactory;
    private static final ThreadLocal threadSession = new ThreadLocal();
    private static final ThreadLocal threadInterceptor = new ThreadLocal();
    private static String CONFIG_FILE_LOCATION = "/hibernate.cfg.xml";
    private static Logger logger = Logger.getLogger(HibernateUtil.class);

    private static String configFile = CONFIG_FILE_LOCATION;

    static {
        try {
            configuration = new Configuration();
            sessionFactory = configuration.configure(configFile).buildSessionFactory();
        } catch (Throwable ex) {
            LogEvent.logError("HibernateUtil", "static constructor", "Building SessionFactory failed. " + ex.toString());
            throw new ExceptionInInitializerError(ex);
        }
    }

    /**
     * Returns the SessionFactory used for this static class.
     *
     * @return SessionFactory
     */

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    /**
     * Returns the original Hibernate configuration.
     *
     * @return Configuration
     */

    public static Configuration getConfiguration() {
        return configuration;
    }

    /**
     * Rebuild the SessionFactory with the static Configuration.
     */
    public static void rebuildSessionFactory() throws RuntimeException {
        synchronized (sessionFactory) {
            try {
                sessionFactory = getConfiguration().buildSessionFactory();
            } catch (Exception ex) {
                LogEvent.logError("HibernateUtil", "rebuildSessionFactory()", ex.toString());
                throw new RuntimeException("Error in rebuildSessionFactory()", ex);
            }
        }
    }

    /**
     * Rebuild the SessionFactory with the given Hibernate Configuration.
     *
     * @param cfg
     */

    public static void rebuildSessionFactory(Configuration cfg) throws RuntimeException {
        synchronized (sessionFactory) {
            try {
                sessionFactory = cfg.buildSessionFactory();
                configuration = cfg;
            } catch (Exception ex) {
                LogEvent.logError("HibernateUtil", "rebuildSessionFactory()", ex.toString());
                throw new RuntimeException("Error in rebuildSessionFactory()", ex);
            }
        }
    }

    /**
     * Retrieves the current Session local to the thread.
     * <p/>
     * <p/>
     * If no Session is open, opens a new Session for the running thread.
     *
     * @return Session
     */
    public static Session getSession() throws RuntimeException {
        Session s = (Session) threadSession.get();
        try {
            if (s == null || !s.isOpen()) {
                if (s != null && !s.isOpen())
                    LogEvent.logWarn(HibernateUtil.class.getSimpleName(), "getSession()", "Session was not null but was closed.");
                LogEvent.logDebug("HibernateUtil", "getSession()", "Opening new Session for this thread.");
                if (getInterceptor() != null) {
                    LogEvent.logDebug("HibernateUtil", "getSession()", "Using interceptor: " + getInterceptor().getClass());
                    s = sessionFactory.withOptions().interceptor(getInterceptor()).openSession();
                } else {
                    s = sessionFactory.openSession();
                }
                threadSession.set(s);
            }
        } catch (HibernateException ex) {
            LogEvent.logError("HibernateUtil", "getSession()", ex.toString());
            throw new RuntimeException("Error in getSession()", ex);
        }
        s.getSession().disconnect();
        return s;
    }

    /**
     * Closes the Session local to the thread.
     */

    public static void closeSession() throws RuntimeException {
        try {
            Session s = (Session) threadSession.get();
            if (s != null && s.isOpen()) {
                try {
                    Transaction transaction = s.getTransaction();
                    if (transaction != null && transaction.isActive()) {
                        transaction.commit();
                    }
                } catch (HibernateException e) {
                    logger.error(e, e);
                }
                LogEvent.logDebug("HibernateUtil", "closeSession()", "Closing Session of this thread.");
                s.close();
            }
            threadSession.remove();
        } catch (HibernateException ex) {
            throw new RuntimeException("Error in closeSession()", ex);
        }
    }

    /**
     * Register a Hibernate interceptor with the current thread.
     * <p/>
     * <p/>
     * Every Session opened is opened with this interceptor after
     * registration. Has no effect if the current Session of the
     * thread is already open, effective on next close()/getSession().
     */
    public static void registerInterceptor(Interceptor interceptor) {
        threadInterceptor.set(interceptor);
    }

    private static Interceptor getInterceptor() {
        Interceptor interceptor =
                (Interceptor) threadInterceptor.get();
        return interceptor;
    }

    public static void closeSessionFactory() {
        org.hibernate.impl.SessionFactoryImpl sf = (org.hibernate.impl.SessionFactoryImpl) sessionFactory;
        org.hibernate.connection.ConnectionProvider conn = sf.getConnectionProvider();
        if (conn instanceof C3P0ConnectionProvider) {
            ((C3P0ConnectionProvider) conn).close();
        }
        sessionFactory.close();
    }
}
