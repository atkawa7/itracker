package org.itracker;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.XmlDataSet;
import org.dbunit.operation.InsertOperation;
import org.springframework.orm.hibernate3.LocalSessionFactoryBean;
import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

public abstract class AbstractDependencyInjectionTest extends AbstractDependencyInjectionSpringContextTests {

    private DataSource dataSource;
    private LocalSessionFactoryBean sessionFactoryBean;
    public ClassLoader classLoader;
    public List dataSets;

    protected AbstractDependencyInjectionTest() {
        classLoader = getClass().getClassLoader();
    }

    protected void onSetUp() throws Exception {
        getSessionFactoryBean().createDatabaseSchema();

        dataSets = getDataSet();

        Connection connection = getDataSource().getConnection();

        DatabaseConnection dbConnection = new DatabaseConnection( connection );

        if( dataSets != null ) {
            for( Iterator iterator = dataSets.iterator(); iterator.hasNext(); ) {
                IDataSet dataSet = (IDataSet)iterator.next();
                InsertOperation.INSERT.execute( dbConnection, dataSet );
            }
        }

        if( !connection.getAutoCommit() ) {
            connection.commit();
        }

    }

    protected void onTearDown() throws Exception {

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

        sessionFactoryBean.destroy();

    }

    private List getDataSet() throws Exception {
        dataSets = new ArrayList();

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

}
