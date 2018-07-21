package org.bahmni.notifications.feed.transaction;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.resource.transaction.spi.TransactionStatus;
import org.ict4h.atomfeed.jdbc.JdbcConnectionProvider;
import org.ict4h.atomfeed.transaction.AFTransactionManager;
import org.ict4h.atomfeed.transaction.AFTransactionWork;

import java.sql.Connection;
import java.sql.SQLException;


public class AtomFeedHibernateTransactionManager implements AFTransactionManager, JdbcConnectionProvider {

    private enum TxStatus {ONGOING, NEW}

    @Override
    public <T> T executeWithTransaction(AFTransactionWork<T> action) throws RuntimeException {
        TxStatus transactionStatus = null;
        try {
            transactionStatus = getTransactionStatus();
            if (transactionStatus.equals(TxStatus.NEW)) {
                startTransaction();
            }
            T result = action.execute();
            if (transactionStatus.equals(TxStatus.NEW)) {
                commit();
            }
            return result;
        } catch (Exception e) {
            if ((transactionStatus != null) && (transactionStatus.equals(TxStatus.NEW))) {
                rollback();
            }
            throw new RuntimeException(e);
        }
    }

    private TxStatus getTransactionStatus() {
        Transaction transaction = getCurrentSession().getTransaction();
        if (transaction != null) {
            if (transaction.isActive()) {
                return TxStatus.ONGOING;
            }
        }
        return TxStatus.NEW;
    }

    @Override
    public Connection getConnection() throws SQLException {
        //TODO: ensure that only connection associated with current thread current transaction is given
        return getCurrentSession().getSessionFactory(). getSessionFactoryOptions().getServiceRegistry()
                . getService(ConnectionProvider.class).getConnection();
    }

    public void startTransaction() {
        Transaction transaction = getCurrentSession().getTransaction();
        if (transaction == null || !transaction.isActive()) {
            getCurrentSession().beginTransaction();
        }
    }

    public void commit() {
        Transaction transaction = getCurrentSession().getTransaction();
        if (!transaction.getStatus().isOneOf(TransactionStatus.COMMITTED)) {
            transaction.commit();
        }
    }

    public void rollback() {
        Transaction transaction = getCurrentSession().getTransaction();
        if (!transaction.getStatus().isOneOf(TransactionStatus.ROLLED_BACK)) {
            transaction.rollback();
        }
    }

    private Session getCurrentSession() {
        return HibernateUtil.getSession();
    }

}
