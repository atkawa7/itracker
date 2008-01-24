package org.itracker;

import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.XmlDataSet;
import org.dbunit.operation.InsertOperation;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.internal.runners.JUnit4ClassRunner;
import org.junit.runner.RunWith;
import org.springframework.orm.hibernate3.LocalSessionFactoryBean;
import org.springframework.orm.hibernate3.SessionHolder;
import org.springframework.test.AbstractDependencyInjectionSpringContextTests;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@RunWith(JUnit4ClassRunner.class)
public abstract class AbstractDependencyInjectionTest extends AbstractDependencyInjectionSpringContextTests {

    private DataSource dataSource;
    private LocalSessionFactoryBean sessionFactoryBean;
    public ClassLoader classLoader;
    public List<IDataSet> dataSets;
    private SessionFactory sessionFactory;

    protected AbstractDependencyInjectionTest() {
        classLoader = getClass().getClassLoader();
    }

    @Override
    public void onSetUp() throws Exception {

        sessionFactory = (SessionFactory) applicationContext.getBean("sessionFactory");
        Session session = sessionFactory.openSession();
        TransactionSynchronizationManager.bindResource(sessionFactory, new SessionHolder(session));

        dataSets = getDataSet();

        Connection connection = getDataSource().getConnection();

        DatabaseConnection dbConnection = new DatabaseConnection( connection );

        if( dataSets != null ) {
            for( Iterator<IDataSet> iterator = dataSets.iterator(); iterator.hasNext(); ) {
                IDataSet dataSet = (IDataSet)iterator.next();
                InsertOperation.INSERT.execute( dbConnection, dataSet );
            }
        }

        if( !connection.getAutoCommit() ) {
            connection.commit();
        }

    }

    @Override
    public void onTearDown() throws Exception {

        Connection connection = getDataSource().getConnection();

        DatabaseConnection dbConnection = new DatabaseConnection( connection );

        if( dataSets != null ) {
            for( int i = dataSets.size() - 1; i >= 0; i-- ) {
                IDataSet dataSet = (IDataSet)dataSets.get( i );
                InsertOperation.DELETE_ALL.execute( dbConnection, dataSet );
            }
        }

        if( !connection.getAutoCommit() ) {
            connection.commit();
        }

        TransactionSynchronizationManager.unbindResource(sessionFactory);
        sessionFactoryBean.destroy();

    }

    private List<IDataSet> getDataSet() throws Exception {
    	List<IDataSet> dataSets = new ArrayList<IDataSet>();

        String[] aDataSet = getDataSetFiles();

        for( int i = 0; i < aDataSet.length; i++ ) {
            IDataSet dataset = new XmlDataSet( classLoader.getResourceAsStream( aDataSet[i] ) );

            dataSets.add( dataset );
        }

        return dataSets;
    }

    protected String[] getDataSetFiles() {
        return null;
    }

    public LocalSessionFactoryBean getSessionFactoryBean() {
        return sessionFactoryBean;
    }

    public void setSessionFactoryBean(
            LocalSessionFactoryBean sessionFactoryBean ) {
        this.sessionFactoryBean = sessionFactoryBean;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource( DataSource dataSource ) {
        this.dataSource = dataSource;
    }

    @Before
    public final void callSetup() throws Exception {
        super.setUp();
    }

    @After
    public final void callTeardown() throws Exception {
        super.tearDown();
    }

}
